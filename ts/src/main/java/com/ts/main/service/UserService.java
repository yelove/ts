package com.ts.main.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ts.main.bean.model.Findpw;
import com.ts.main.bean.model.User;
import com.ts.main.mapper.FindpwMapper;
import com.ts.main.mapper.UserMapper;
import com.ts.main.utils.MD5Tools;

@Service
public class UserService {

	private Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private FindpwMapper findpwMapper;

	@Autowired
	private RedisService<Long> idRedisService;

	@Autowired
	private RedisService<User> userRedisService;

	private static Cache<String, User> userCache = CacheBuilder.newBuilder().softValues()
			.expireAfterWrite(2, TimeUnit.HOURS).initialCapacity(512).maximumSize(32768).build();

	private static final String TSNO = "inc_tsno";

	@PostConstruct
	public void initTsNo() throws Exception {
		Long maxtsno = userMapper.getMaxNoUser();
		if (maxtsno < 1) {
			maxtsno = 10000l;
		}
		Long noid = idRedisService.initIncrement(TSNO,maxtsno);
		noid = idRedisService.getIncrement(TSNO);
		logger.info("maxtsno is {}", noid);
	}

	public boolean saveUser(User user) {
		user.setCreatetime(System.currentTimeMillis());
		user.setTsno(idRedisService.getIncrement(TSNO));
		user.setPassword(MD5Tools.MD5(user.getPassword()));
		user.setState(true);
		userMapper.insert(user);
		putUser(user);
		return true;
	}

	private void putUser(User user) {
		userCache.put(String.valueOf(user.getId()), user);
		userRedisService.hSet(RedisService.USER_KEY, String.valueOf(user.getId()), user);
	}

	public User getUserBiIdWithCache(Long uid) {
		String uidstr = String.valueOf(uid);
		User user = userCache.getIfPresent(uidstr);
		if(null==user){
			user = userRedisService.hGet(RedisService.USER_KEY, uidstr);
			if (null == user) {
				user = userMapper.selectByPrimaryKey(uid);
			}else{
				putUser(user);
			}
		}
		return user;
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
	
	public Findpw getFpById(String id){
		return findpwMapper.selectByPrimaryKey(id);
	}
	
	public String insertFindpw(String email,Long uid){
		List<Findpw> fplis = findpwMapper.selectByUid(uid);
		if(!fplis.isEmpty()){
			return fplis.get(0).getId();
		}
		Findpw fp = new Findpw();
		Long ct = System.currentTimeMillis();
		fp.setId(MD5Tools.MD5(email+ct));
		fp.setUid(uid);
		fp.setCreatetime(ct);
		fp.setUpdatetime(ct);
		fp.setFtype(0);
		int i = findpwMapper.insert(fp);
		if(i>0){
			return fp.getId();
		}else{
			return null;
		}
	}

}
