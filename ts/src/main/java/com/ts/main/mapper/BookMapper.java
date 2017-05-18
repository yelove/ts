package com.ts.main.mapper;

import java.util.List;
import java.util.Map;

import com.ts.main.bean.model.Book;

public interface BookMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Book record);

    int insertSelective(Book record);

    Book selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Book record);

    int updateByPrimaryKey(Book record);
    
    List<Book> getBookList(Map<String,Object> param);

	int getMineTotal(Long userid);

}