package com.ts.main.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ts.main.bean.model.User;
import com.ts.main.mapper.UserMapper;

@Service
public class UserService {
	
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	
//	@Autowired
//	private UserDao userDao;
	
	@Autowired
	private UserMapper userMapper;
	
	private static ConcurrentHashMap<Long,User> userCache =  new ConcurrentHashMap<Long,User>();
	
	private static ConcurrentHashMap<String,Long> usermapingCache =  new ConcurrentHashMap<String,Long>();
	
	private static ConcurrentHashMap<Long,Long> userbookidmapingCache =  new ConcurrentHashMap<Long,Long>();
	
	private static final AtomicLong tsnoMaker = new AtomicLong();
	
	@PostConstruct
	public void initTsNo() throws Exception{
		long maxtsno = userMapper.getMaxNoUser();
		logger.info("maxtsno is {}",maxtsno);
		tsnoMaker.set(maxtsno);
	}
	
	public boolean saveUser(User user){
		user.setCreatetime(System.currentTimeMillis());
		user.setTsno(tsnoMaker.incrementAndGet());
		user.setState(true);
		userMapper.insert(user);
		userCache.put(user.getId(), user);
		usermapingCache.put(user.getEmail(), user.getId());
		userbookidmapingCache.put(user.getTsno(), user.getId());
		return true;
	}
	
	public User getUserBiIdWithCache(Long uid){
		User user = userCache.get(uid);
		if(null!=user){
			return user;
		}
		user = userMapper.selectByPrimaryKey(uid);
		if(null!=user){
			userCache.put(user.getId(), user);
			usermapingCache.put(user.getEmail(), user.getId());
			userbookidmapingCache.put(user.getTsno(), user.getId());
			return user;
		}
		return null;
	}
	
	public User getUserByName(String name){
		User user = null;
		if(name.contains("@")){
			user = userMapper.getUserByEmail(name);
		}else{
			user = userMapper.getUserByNo(Long.parseLong(name));
		}
		return user;
	}
	
	public void updateUser(User user){
		int i = userMapper.updateByPrimaryKeySelective(user);
		if(i>0){
			userCache.put(user.getId(), user);
		}
	}

	public void logout(User user) {
		user.setUpdatetime(System.currentTimeMillis());
		userCache.put(user.getId(), user);
	}
	
	public void updateUserForLastLog(User user){
		userMapper.updateUserForLastLog(user.getId(), System.currentTimeMillis());
	}

}
