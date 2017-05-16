/**
 * 
 */
package com.ts.main.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.ts.main.bean.Book;
import com.ts.main.bean.vo.Page;

/**
 * @author hasee
 *
 */
@Service
public class BookDao {
	
	@Autowired
	private JdbcTemplate template;
	
	public Long saveBook(final Book book){
		KeyHolder kh = new GeneratedKeyHolder();
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(
						"INSERT into `book` (createtime,updatetime,isopen,text,userid,isdel,marktime) VALUES (?,?,?,?,?,0,?);");
				ps.setLong(1, book.getCreatetime());
				ps.setLong(2, book.getUpdatetime());
				ps.setInt(3, book.getIsopen());
				ps.setString(4, book.getText());
				ps.setLong(5, book.getUserid());
				ps.setLong(6, book.getMarktime());
				return ps;
			}
		}, kh);
		return kh.getKey().longValue();
	}
	
	public List<Book> getBookListByUserId(Long userid,int page,int size){
		String sql = "select * from `book` where isdel=0 and userid = ? order by createtime desc limit ?,?";
		List<Map<String, Object>> rsm = template.queryForList(sql,
				new Object[] { userid, size *page , size });
		List<Book> rsod = new ArrayList<Book>();
		for (Map<String, Object> rs : rsm) {
			Book book = new Book();
			try {
				BeanUtils.populate(book, rs);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rsod.add(book);
		}
		return rsod;
	}

	public List<Book> getHotBooks(Page page) {
		String sql = "select * from `book` where isdel=0 order by createtime,praisenum desc limit ?,?";
		List<Map<String, Object>> rsm = template.queryForList(sql,
				new Object[] { page.getLimit()*page.getPage() , page.getLimit() });
		List<Book> rsod = new ArrayList<Book>();
		for (Map<String, Object> rs : rsm) {
			Book book = new Book();
			try {
				BeanUtils.populate(book, rs);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rsod.add(book);
		}
		return rsod;
	}
	
	public List<Book> getBookListByUserIdAndBookId(Long userid,Long bookid){
		String sql = "select * from `book` where isdel=0 and userid = ? and id<=? order by createtime desc limit 3";
		List<Map<String, Object>> rsm = template.queryForList(sql,
				new Object[] { userid,bookid});
		List<Book> rsod = new ArrayList<Book>();
		for (Map<String, Object> rs : rsm) {
			Book book = new Book();
			try {
				BeanUtils.populate(book, rs);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rsod.add(book);
		}
		return rsod;
	}

	public Long getMineTotal(long id) {
		String sql = "select count(1) as total from `book` where isdel=0 and userid = ?";
		Map<String, Object> map = template.queryForMap(sql,id);
		Long x = (Long) map.get("total");
		return x;
	}

	public List<Book> getMine(long id, Page page) {
		String sql = "select * from `book` where isdel=0 and userid = ? order by createtime desc limit ?,?";
		List<Map<String, Object>> rsm = template.queryForList(sql,
				new Object[] { id,page.getLimit()*page.getPage() , page.getLimit()});
		List<Book> rsod = new ArrayList<Book>();
		for (Map<String, Object> rs : rsm) {
			Book book = new Book();
			try {
				BeanUtils.populate(book, rs);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rsod.add(book);
		}
		return rsod;
	}

}
