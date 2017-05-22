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
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ts.main.bean.model.Book;
import com.ts.main.bean.vo.Page;
import com.ts.main.utils.TimeUtils4book;

/**
 * @author hasee
 *
 */
public class BookDao {

	@Autowired
	private JdbcTemplate template;

	public Long saveBook(final Book book) {
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

	public List<Book> getBookListByUserId(Long userid, int page, int size) {
		String sql = "select * from `book` where isdel=0 and userid = ? order by createtime desc limit ?,?";
		List<Map<String, Object>> rsm = template.queryForList(sql, new Object[] { userid, size * page, size });
		List<Book> rsod = new ArrayList<Book>();
		for (Map<String, Object> rs : rsm) {
			Book book = new Book();
			try {
				BeanUtils.populate(book, rs);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rsod.add(book);
		}
		return rsod;
	}

	private Long getStartCreatetime(int term) {
		switch (term) {
		case 4:
			return TimeUtils4book.getBefore(365l, TimeUnit.DAYS);
		case 3:
			return TimeUtils4book.getBefore(30l, TimeUnit.DAYS);
		case 2:
			return TimeUtils4book.getBefore(7l, TimeUnit.DAYS);
		default:
			return TimeUtils4book.getBefore(1l, TimeUnit.DAYS);
		}
	}

	public List<Book> getHotBooks(Page page) {
		String sql = "select * from `book` where isdel=0 and isopen=0 and createtime>? order by praisenum desc,createtime desc limit ?,?";
		List<Map<String, Object>> rsm = template.queryForList(sql,
				new Object[] { getStartCreatetime(page.getTerm()), page.getLimit() * page.getPage(), page.getLimit() });
		List<Book> rsod = new ArrayList<Book>();
		for (Map<String, Object> rs : rsm) {
			Book book = new Book();
			try {
				BeanUtils.populate(book, rs);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rsod.add(book);
		}
		return rsod;
	}

	public List<Book> getBookListByUserIdAndBookId(Long userid, Long bookid) {
		String sql = "select * from `book` where isdel=0 and userid = ? and id<=? order by createtime desc limit 3";
		List<Map<String, Object>> rsm = template.queryForList(sql, new Object[] { userid, bookid });
		List<Book> rsod = new ArrayList<Book>();
		for (Map<String, Object> rs : rsm) {
			Book book = new Book();
			try {
				BeanUtils.populate(book, rs);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rsod.add(book);
		}
		return rsod;
	}

	public Long getMineTotal(long id) {
		String sql = "select count(1) as total from `book` where isdel=0 and userid = ?";
		Map<String, Object> map = template.queryForMap(sql, id);
		Long x = (Long) map.get("total");
		return x;
	}

	public List<Book> getMine(long id, Page page) {
		String sql = "select * from `book` where isdel=0 and userid = ? order by createtime desc limit ?,?";
		List<Map<String, Object>> rsm = template.queryForList(sql,
				new Object[] { id, page.getLimit() * page.getPage(), page.getLimit() });
		List<Book> rsod = new ArrayList<Book>();
		for (Map<String, Object> rs : rsm) {
			Book book = new Book();
			try {
				BeanUtils.populate(book, rs);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rsod.add(book);
		}
		return rsod;
	}

}
