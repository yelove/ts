package com.ts.main.mapper;

import org.apache.ibatis.annotations.Param;

import com.ts.main.bean.model.User;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

	Long getMaxNoUser();

	int updateUserForLastLog(@Param("id")Long id, @Param("updatetime")Long currentTimeMillis);

	User getUserByName(@Param("name")String name);
	
	User getUserByEmail(@Param("email")String email);

	User getUserByNo(@Param("tsno")Long parseLong);
}