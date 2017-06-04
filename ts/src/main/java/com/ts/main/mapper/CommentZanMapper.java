package com.ts.main.mapper;

import java.util.List;

import com.ts.main.bean.model.CommentZan;
import com.ts.main.bean.model.CommentZanKey;

public interface CommentZanMapper {
    int deleteByPrimaryKey(CommentZanKey key);

    int insert(CommentZan record);

    int insertSelective(CommentZan record);

    CommentZan selectByPrimaryKey(CommentZanKey key);

    int updateByPrimaryKeySelective(CommentZan record);

    int updateByPrimaryKey(CommentZan record);
    
    /**
     * @param bookid
     * @return 根据cmtid查询赞过的userid
     */
    List<Long> selectByCommentId(Long cmtid);
}