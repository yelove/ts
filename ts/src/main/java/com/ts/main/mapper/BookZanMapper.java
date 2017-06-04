package com.ts.main.mapper;

import java.util.List;

import com.ts.main.bean.model.BookZan;
import com.ts.main.bean.model.BookZanKey;

public interface BookZanMapper {
    int deleteByPrimaryKey(BookZanKey key);

    int insert(BookZan record);

    int insertSelective(BookZan record);

    BookZan selectByPrimaryKey(BookZanKey key);

    int updateByPrimaryKeySelective(BookZan record);

    int updateByPrimaryKey(BookZan record);
    
    /**
     * @param bookid
     * @return 根据bookid查询赞过的userid
     */
    List<Long> selectByBookId(Long bookid);
}