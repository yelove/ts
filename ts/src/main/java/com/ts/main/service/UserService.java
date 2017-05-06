package com.ts.main.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ts.main.bean.User;
import com.ts.main.dao.UserDao;

@Service
public class UserService {
	
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserDao userDao;
	
	private static ConcurrentHashMap<String,User> userCache =  new ConcurrentHashMap<String,User>();
	
	private static final AtomicLong tsnoMaker = new AtomicLong();
	
	@PostConstruct
	public void initTsNo() throws Exception{
		Long maxtsno = userDao.getMaxNoUser();
		if(null==maxtsno){
			throw new Exception("initTsNo fail");
		}
		logger.info("maxtsno is {}",maxtsno);
		tsnoMaker.set(maxtsno);
	}
	
	public boolean saveUser(User user){
		user.setCreatetime(System.currentTimeMillis());
		user.setTsno(tsnoMaker.getAndAdd(1));
		long id = userDao.saveUser(user);
		userCache.put(user.getEmail(), user);
		user.setId(id);
		return true;
	}
	
	public User getUserByName(String name){
		User user = null;
		if(name.contains("@")){
			user = userDao.getUser(name);
		}else{
			user = userDao.getUser(Long.parseLong(name));
		}
		return user;
	}

	public void logout(User user) {
		user.setUpdatetime(System.currentTimeMillis());
		userCache.put(user.getName(), user);
	}
	
	public void updateUserForLastLog(User user){
		userDao.updateUserForLastLog(user.getId(), System.currentTimeMillis());
	}

}
