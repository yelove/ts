/**
 * 
 */
package com.ts.main.service;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ts.main.bean.Book;
import com.ts.main.bean.vo.BookVo;
import com.ts.main.bean.vo.Page;
import com.ts.main.dao.BookDao;

/**
 * @author hasee
 *
 */
@Service
public class BookService {
	
	@Autowired
	private BookDao bookDao;
	
	
	public Long saveBook(Book book){
		book.setCreatetime(System.currentTimeMillis());
		book.setUpdatetime(book.getCreatetime());
		return bookDao.saveBook(book);
	}
	
	public List<BookVo> getBookVo123(Long userid){
		List<Book> lb = bookDao.getBookListByUserId(userid, 0, 3);
		if(null==lb||lb.size()==0){
			return null;
		}
		return covent(lb);
	}
	
	private List<BookVo> covent(List<Book> bl){
		List<BookVo> bvl = new ArrayList<BookVo>();
		for(Book book : bl){
			bvl.add(covent(book));
		}
		return bvl;
	}
	
	private BookVo covent(Book book){
		BookVo bkv = new BookVo();
		try {
			BeanUtils.copyProperties(bkv, book);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		bkv.setMarkdate(df.format(new Date(bkv.getMarktime())));
		bkv.setCreatdate(df.format(new Date(bkv.getCreatetime())));
		bkv.setUpdatedate(df.format(new Date(bkv.getUpdatetime())));
		return bkv;
	}

	/**
	 * @param page
	 * @return 热评查询 原始版 直接查数据库30条
	 */
	public List<BookVo> getHotBooks(Page page) {
		List<BookVo> bvl = new ArrayList<BookVo>();
		List<Book> booklis = bookDao.getHotBooks(page);
		for(Book book : booklis){
			BookVo e = covent(book);
			e.setNearlist(covent(bookDao.getBookListByUserIdAndBookId(book.getUserid(), book.getId())));
			bvl.add(e);
		}
		return bvl;
	}

	public Long getMineTotal(long id) {
		Long total = bookDao.getMineTotal(id);
		return total;
	}

	public List<BookVo> getMine(long id, Page page) {
		List<Book> booklis = bookDao.getMine(id,page);
		return covent(booklis);
	}

}
