package com.ts.main.mapper;

import java.util.List;

import com.ts.main.bean.model.Mission;

public interface MissionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Mission record);

    int insertSelective(Mission record);

    Mission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Mission record);

    int updateByPrimaryKey(Mission record);
    
    List<Mission> selectMissionByDate(Long date);
    
    List<Mission> selectAll();
}