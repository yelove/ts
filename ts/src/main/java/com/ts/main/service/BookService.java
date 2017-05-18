/**
 * 
 */
package com.ts.main.service;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ts.main.bean.model.Book;
import com.ts.main.bean.vo.BookVo;
import com.ts.main.bean.vo.Page;
import com.ts.main.mapper.BookMapper;
import com.ts.main.util.TimeUtils;

/**
 * @author hasee
 *
 */
@Service
public class BookService {
	
//	@Autowired
//	private BookDao bookDao;
	
	@Autowired
	BookMapper bookMapper;
	
//	private static Cache<String,List<Book>> hot24h = CacheBuilder.newBuilder()
//			.expireAfterAccess(7, TimeUnit.SECONDS).maximumSize(2048).build();
//	private static Cache<String,List<Book>> newest = CacheBuilder.newBuilder()
//			.expireAfterAccess(7, TimeUnit.SECONDS).maximumSize(2048).build();
//	private static Cache<String,List<Book>> hot7d = CacheBuilder.newBuilder()
//			.expireAfterAccess(7, TimeUnit.SECONDS).maximumSize(2048).build();
	
	
	@PostConstruct
	public void intiHotBook(){
		
	}
	
	
	public Long saveBook(Book book){
		book.setCreatetime(System.currentTimeMillis());
		book.setUpdatetime(book.getCreatetime());
		book.setIsdel(0);
		bookMapper.insertSelective(book);
		return book.getId();
	}
	
	public List<BookVo> getBookVo123(Long userid){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userid", userid);
		map.put("page", 0);
		map.put("limit", 3);
		map.put("orderbystr", "createtime desc ");
		List<Book> lb = bookMapper.getBookList(map);
		if(null==lb||lb.size()==0){
			return null;
		}
		return covent(lb);
	}
	
	/**
	 * @param page
	 * @return 热评查询 原始版 直接查数据库30条
	 */
	public List<BookVo> getHotBooks(Page page) {
		List<BookVo> bvl = new ArrayList<BookVo>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("page", page.getPage());
		map.put("limit", page.getLimit());
		map.put("isopen", 0);
		map.put("createtime", getStartCreatetime(page.getTerm()));
		map.put("orderbystr", "praisenum desc,createtime desc ");
		List<Book> booklis = bookMapper.getBookList(map);
		for(Book book : booklis){
			BookVo e = covent(book);
//			e.setNearlist(covent(bookDao.getBookListByUserIdAndBookId(book.getUserid(), book.getId())));
			bvl.add(e);
		}
		return bvl;
	}

	public int getMineTotal(long userid) {
		int total = bookMapper.getMineTotal(userid);
		return total;
	}

	public List<BookVo> getMine(long userid, Page page) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userid", userid);
		map.put("page", page.getPage());
		map.put("limit", page.getLimit());
		map.put("orderbystr", "createtime desc ");
		List<Book> booklis = bookMapper.getBookList(map);
		return covent(booklis);
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
	
	private Long getStartCreatetime(int term) {
		switch (term) {
		case 4:
			return TimeUtils.getBefore(365l, TimeUnit.DAYS);
		case 3:
			return TimeUtils.getBefore(30l, TimeUnit.DAYS);
		case 2:
			return TimeUtils.getBefore(7l, TimeUnit.DAYS);
		default:
			return TimeUtils.getBefore(1l, TimeUnit.DAYS);
		}
	}

}
