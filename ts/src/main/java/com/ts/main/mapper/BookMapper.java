package com.ts.main.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

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

	/**
	 * @return 初始化数据时使用 获取所有的日记 id 和对应的用户id
	 */
	List<Book> getAllBookIdAndUserId();

	/**
	 * @param userid
	 * @return 获取自己日记id列表
	 */
	List<Long> getMineBookId(@Param("userid")Long userid);


	/**
	 * @param id123
	 * @return 根据id获取最新的日记
	 */
	List<Book> getNewestBook(@Param("bigid")Long bigid);

	/**
	 * @param before
	 * @return 根据不同时间获取 最热
	 */
	List<Book> getHotBook(@Param("before")Long before);
	
	/**
	 * @param bookid
	 * @return 根据id更新评论数量 数量+1
	 */
	int updateBookCommentSize(Long id);
	
	/**
	 * @param bookid
	 * @return 根据id更新评论数量 数量-1
	 */
	int delBookCommentSize(Long id);

}