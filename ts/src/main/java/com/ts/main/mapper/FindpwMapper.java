package com.ts.main.mapper;

import java.util.List;

import com.ts.main.bean.model.Findpw;

public interface FindpwMapper {
    int deleteByPrimaryKey(String id);

    int insert(Findpw record);

    int insertSelective(Findpw record);

    Findpw selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Findpw record);

    int updateByPrimaryKey(Findpw record);

	List<Findpw> selectByUid(Long uid);
}