package com.ts.main.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ts.main.bean.model.User;
import com.ts.main.mapper.UserMapper;

@Service
public class UserService {

	private Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private RedisService<Long> idRedisService;

	@Autowired
	private RedisService<User> userRedisService;

	private static Cache<String, User> userCache = CacheBuilder.newBuilder().softValues()
			.expireAfterWrite(2, TimeUnit.HOURS).initialCapacity(512).maximumSize(32768).build();

	private static final String TSNO = "inc_tsno";

	@PostConstruct
	public void initTsNo() throws Exception {
		long maxtsno = userMapper.getMaxNoUser();
		if (maxtsno < 1) {
			maxtsno = 10000;
		}
		Long noid = idRedisService.initIncrement(TSNO,maxtsno);
		noid = idRedisService.getIncrement(TSNO);
		logger.info("maxtsno is {}", noid);
	}

	public boolean saveUser(User user) {
		user.setCreatetime(System.currentTimeMillis());
		user.setTsno(idRedisService.getIncrement(TSNO));
		user.setState(true);
		userMapper.insert(user);
		putUser(user);
		return true;
	}

	private void putUser(User user) {
		userCache.put(String.valueOf(user.getId()), user);
		userRedisService.hSet(RedisService.USER_KEY, String.valueOf(user.getId()), user);
	}

	public User getUserBiIdWithCache(final Long uid) {
		try {
			return userCache.get(String.valueOf(uid), new Callable<User>() {

				@Override
				public User call() throws Exception {
					User user = userRedisService.hGet(RedisService.USER_KEY, String.valueOf(uid));
					if (null == user) {
						user = userMapper.selectByPrimaryKey(uid);
					}
					return user;
				}

			});
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public User getUserByName(String name) {
		User user = null;
		if (name.contains("@")) {
			user = userMapper.getUserByEmail(name);
		} else {
			user = userMapper.getUserByNo(Long.parseLong(name));
		}
		return user;
	}

	public void updateUser(User user) {
		user.setUpdatetime(System.currentTimeMillis());
		int i = userMapper.updateByPrimaryKeySelective(user);
		if (i > 0) {
			putUser(user);
		}
	}

	public void updateUserForLastLog(User user) {
		userMapper.updateUserForLastLog(user.getId(), System.currentTimeMillis());
	}

}
