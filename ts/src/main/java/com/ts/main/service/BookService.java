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
			bvl.add(bkv);
		}
		return bvl;
	}

}
