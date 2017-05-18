/**
 * 
 */
package com.ts.main.service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author hasee
 *
 */
@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, Serializable> redisTemplate;

	/**
	 * redis key 默认超时时间 7天
	 */
	public static final long REDIS_TIMEOUT = 7;

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) redisTemplate.opsForValue().get(key);
	}

	public void set(String key, Serializable value) {
		this.set(key, value, REDIS_TIMEOUT, TimeUnit.DAYS);
	}

	public void set(String key, Serializable value, long timeout, TimeUnit timeunit) {
		redisTemplate.opsForValue().set(key, value, timeout, timeunit);
	}

	public boolean setNx(final String key, final long seconds) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				boolean x = connection.setNX(key.getBytes(), new byte[] { 0 });
				if (x) {
					connection.expire(key.getBytes(), seconds);
				}
				return x;
			}
		});
	}

	public boolean exist(final String key) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(key.getBytes());
			}
		});
	}

}
