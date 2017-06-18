package com.ts.main.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ts.main.bean.model.UserMission;
import com.ts.main.bean.model.UserMissionKey;

public interface UserMissionMapper {
    int deleteByPrimaryKey(UserMissionKey key);

    int insert(UserMission record);

    int insertSelective(UserMission record);

    UserMission selectByPrimaryKey(UserMissionKey key);

    int updateByPrimaryKeySelective(UserMission record);

    int updateByPrimaryKey(UserMission record);
    
    List<UserMission> selectByUserId(@Param("userid")Long userid);
    
    List<UserMission> selectByUserIdAndDate(@Param("userid")Long userid,@Param("date")Long date,@Param("today")Long today);
    
    List<Long> getAllMissionByUserId(@Param("userid")Long userid);
}