/**
 * 
 */
package com.ts.main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

/**
 * @author hasee
 * @param <T>
 *
 */
@Service
public class RedisService<T> {

	@Autowired
	private RedisTemplate<String, T> redisTemplate;

	public static final String BOOK_KEY = "bkid_";
	
	public static final String COMMENT_KEY = "cmtid_";

	public static final String USER_KEY_NAME = "username_";

	/**
	 * redis key 默认超时时间 7天 数据量小情况可用
	 */
	public static final long REDIS_TIMEOUT = 7;

	/**
	 * @param key
	 * @return 单值get
	 */
	public T get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	/**
	 * @param keys
	 * @return 批量get
	 */
	public List<T> getKeys(List<String> keys) {
		return redisTemplate.opsForValue().multiGet(keys);
	}

	/**
	 * @param key
	 * @param value
	 *            单值set 默认7天过期
	 */
	public void set(String key, T value) {
		this.set(key, value, REDIS_TIMEOUT, TimeUnit.DAYS);
	}

	/**
	 * @param key
	 *            删除 慎用 list会直接整个删除 map 不指定hashkey 也会整个删除
	 */
	public void del(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * @param key
	 * @param value
	 * @param timeout
	 * @param timeunit
	 *            单值set 带过期时间
	 */
	public void set(String key, T value, long timeout, TimeUnit timeunit) {
		redisTemplate.opsForValue().set(key, value, timeout, timeunit);
	}

	/**
	 * @param key
	 * @param value
	 * @return 向list添加值 redis的list顺序是从左到右
	 */
	public Long add2List(String key, T value) {
		return redisTemplate.opsForList().rightPush(key, value);
	}
	
	public Long add2ListLeft(String key, T value) {
		return redisTemplate.opsForList().leftPush(key, value);
	}

	/**
	 * @param <T>
	 * @param key
	 * @param values
	 * @return 批量添加
	 */
	public Long addAll2List(String key, List<T> values) {
		return redisTemplate.opsForList().rightPushAll(key, values);
	}
	
	public Long addAll2ListLeft(String key, List<T> values) {
		return redisTemplate.opsForList().leftPushAll(key, values);
	}

	/**
	 * @param key
	 * @return 返回栈顶元素 即最后一个元素
	 */
	public T getListLast(String key,Class<T> type) {
		List<T> x = getLisetinner(key, -1l, -1l,type);
		if (null == x || x.isEmpty()) {
			return null;
		}

		return (T) x.get(0);
	}
	
	public T getListFirst(String key,Class<T> type) {
		List<T> x = getLisetinner(key, 0l, 0l,type);
		if (null == x || x.isEmpty()) {
			return null;
		}

		return (T) x.get(0);
	}

	/**
	 * @param key
	 * @return 获取list 长度
	 */
	public Long getListSize(String key) {
		return redisTemplate.opsForList().size(key);
	}

	/**
	 * @param <T>
	 * @param key
	 * @return 获取list所有元素 不是弹出
	 */
	public List<T> getList(String key, Class<T> type) {
		return getLisetinner(key, 0l, -1l, type);
	}

	public List<T> getLisetinner(final String key, final Long start, final Long end, final Class<T> type) {
		return redisTemplate.execute(new RedisCallback<List<T>>() {
			@Override
			public List<T> doInRedis(RedisConnection connection) throws DataAccessException {
				List<byte[]> x = connection.lRange(key.getBytes(), start, end);
				if (null == x) {
					return null;
				}
				List<T> rs = new ArrayList<T>(x.size());
				for (byte[] xt : x) {
					rs.add(((GenericJackson2JsonRedisSerializer) redisTemplate.getValueSerializer()).deserialize(xt,
							type));
				}
				return rs;
			}
		});
	}
	
	/**
	 * @param key
	 * @param rage
	 * @return 获取list 范围1-200
	 */
	public List<T> getListRage(String key, Long range, Class<T> type) {
		if (range < 1 || range > 200) {
			return null;
		}
		return getLisetinner(key, -range, -1l, type);
	}

	/**
	 * @param key
	 * @param from
	 * @param to
	 * @return 自由获取范围内的值
	 */
	public List<T> getListRageFreedom(String key, Long start, Long end, Class<T> type) {
		return getLisetinner(key, start, end, type);
	}
	
	/**
	 * @param newestbookList
	 * @return 左弹出
	 */
	public T leftPop(String newestbookList) {
		
		return redisTemplate.opsForList().leftPop(newestbookList);
	}
	
	/**
	 * @param key
	 * @param count
	 * @param value
	 * @return 从list中移除某个元素 
	 * 	count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 
		count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值 
		count = 0 : 移除表中所有与 value 相等的值 
	 */
	public long listRemoveValue(String key,long count,T value){
		return redisTemplate.opsForList().remove(key, count, value);
	}

	/**
	 * @param key
	 * @param hashKey
	 * @param value
	 *            单值hash set
	 */
	public void hSet(String key, String hashKey, T value) {
		redisTemplate.opsForHash().put(key, hashKey, value);
	}

	/**
	 * @param key
	 * @param hashKey
	 * @return 单值hash get
	 */
	@SuppressWarnings("unchecked")
	public T hGet(String key, String hashKey) {
		return (T) redisTemplate.opsForHash().get(key, hashKey);
	}

	@SuppressWarnings("unchecked")
	public List<Object> hGetList(String key, @SuppressWarnings("rawtypes") Set hashKey) {
		return redisTemplate.opsForHash().multiGet(key, hashKey);
	}

	/**
	 * @param key
	 * @param hashKey
	 *            单值hash 删除
	 */
	public void hDel(String key, String hashKey) {
		redisTemplate.opsForHash().delete(key, hashKey);
	}

	/**
	 * @param key
	 * @param seconds
	 * @return 分布式缓存锁 带超时时间 使用管道操作
	 */
	public boolean setNx(final String key, final long seconds) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				String rkey = "setnx_" + key;
				boolean x = connection.setNX(rkey.getBytes(), new byte[] { 0 });
				if (x) {
					connection.expire(rkey.getBytes(), seconds);
				}
				return x;
			}
		});
	}

	/**
	 * @param key
	 * @return 判断是否存在值
	 */
	public boolean exist(final String key) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(key.getBytes());
			}
		});
	}


}
