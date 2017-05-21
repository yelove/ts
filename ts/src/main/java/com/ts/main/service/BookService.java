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
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ts.main.bean.model.Book;
import com.ts.main.bean.vo.BookVo;
import com.ts.main.bean.vo.ID123;
import com.ts.main.bean.vo.Page;
import com.ts.main.mapper.BookMapper;
import com.ts.main.util.TimeUtils;

/**
 * @author hasee
 *
 */
@Service
public class BookService {

	/**
	 * 个人日记前缀
	 */
	public static final String UserBook_List = "UserBook_List_";

	public static final String UserBook_List_ALL = "UserBook_List_All_";
	/**
	 * 最新日记列表前缀
	 */
	public static final String NewestBook_List = "NewestBook_List";
	public static final String HotBook24H = "HotBook24H";
	public static final String HotBook7D = "HotBook7D";
	public static final String HotBook30D = "HotBook30D";
	public static final String HotBook1Y = "HotBook1Y";
	public static final String HotBookAll = "HotBookAll";

	public static final Integer PAGE_SIZE = 10;

	@Autowired
	BookMapper bookMapper;

	@Autowired
	RedisService<ID123> redisService;

	@Autowired
	RedisService<Book> bookredisService;

	@Autowired
	RedisService<Long> longredisService;

	private static Cache<String, List<ID123>> BOOKLISTCACHE = CacheBuilder.newBuilder()
			.expireAfterWrite(1, TimeUnit.MINUTES).initialCapacity(2).build();

	private static Cache<String, List<ID123>> HOTBOOKLIST = CacheBuilder.newBuilder().initialCapacity(8).build();

	private static Cache<String, Book> book4096 = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES)
			.initialCapacity(512).maximumSize(4096).build();

	public void intiNewestBook() {
		if (redisService.setNx("Task_newestBook", 10)) {
			List<Book> booklis = null;
			if (redisService.exist(NewestBook_List)) {
				ID123 id123 = redisService.getListFirst(NewestBook_List,ID123.class);
				booklis = bookMapper.getNewestBook(id123.getBkid());
			} else {
				// 初始化 最新 列表
				booklis = bookMapper.getAllBookIdAndUserId();
			}
			if (null == booklis || booklis.size() == 0) {
				return;
			}
			List<ID123> id123Lis = new ArrayList<ID123>(booklis.size());
			Map<Long,Integer> filtermap = new HashMap<Long,Integer>(booklis.size());
			for (Book book : booklis) {
				Long userid = book.getUserid();
				if(filtermap.containsKey(userid)){
					if(filtermap.get(userid)%3==0){
						filtermap.put(userid, filtermap.get(userid)+1);
					}else{
						filtermap.put(userid, filtermap.get(userid)+1);
						continue;
					}
				}else{
					filtermap.put(userid, 1);
				}
				List<Long> minebooklis = getMineBookIds(userid);
				int i = minebooklis.indexOf(book.getId());
				Long id2 = -1l;
				Long id3 = -1l;
				if (minebooklis.size() > i + 1) {
					id2 = minebooklis.get(i + 1);
					if (minebooklis.size() > i + 2) {
						id3 = minebooklis.get(i + 2);
					}
				}
				id123Lis.add(new ID123(book.getId(), id2, id3));
			}
			if (id123Lis.size() <= 200) {
				BOOKLISTCACHE.put(NewestBook_List, id123Lis);
			}
			redisService.addAll2List(NewestBook_List, id123Lis);
		}
	}

	public void initHotBook(int day) {
		if (redisService.setNx("Task_hotBook_" + day, 10)) {
			List<Book> booklis = bookMapper
					.getHotBook(day == 0 ? 0l : TimeUtils.getBefore(new Long(day), TimeUnit.DAYS));
			if (null == booklis || booklis.size() == 0) {
				return;
			}
			List<ID123> id123Lis = new ArrayList<ID123>(booklis.size());
			for (Book book : booklis) {
				List<Long> minebooklis = getMineBookIds(book.getUserid());
				int i = minebooklis.indexOf(book.getId());
				Long id2 = -1l;
				Long id3 = -1l;
				if (minebooklis.size() > i+1) {
					id2 = minebooklis.get(i + 1);
					if (minebooklis.size() > i + 2) {
						id3 = minebooklis.get(i + 2);
					}
				}
				id123Lis.add(new ID123(book.getId(), id2, id3));
			}
			switch (day) {
			case 1:
				HOTBOOKLIST.put(HotBook24H, id123Lis);break;
			case 7:
				HOTBOOKLIST.put(HotBook7D, id123Lis);break;
			case 30:
				HOTBOOKLIST.put(HotBook30D, id123Lis);break;
			case 365:
				HOTBOOKLIST.put(HotBook1Y, id123Lis);break;
			case 0:
				HOTBOOKLIST.put(HotBookAll, id123Lis);break;
			}
		}
	}

	/**
	 * @param day
	 * @param start
	 * @return 热点是 倒叙查询 整体替换原有的list
	 */
	public List<BookVo> queryHotBook(int day, Integer start) {
		String fkey = "";
		switch (day) {
		case 1:
			fkey = HotBook24H;break;
		case 7:
			fkey = HotBook7D;break;
		case 30:
			fkey = HotBook30D;break;
		case 365:
			fkey = HotBook1Y;break;
		case 0:
			fkey = HotBookAll;break;
		}
		List<ID123> list = HOTBOOKLIST.getIfPresent(fkey);
		if (null == list) {
			initHotBook(day);
			list = HOTBOOKLIST.getIfPresent(fkey);
			if (null == list) {
				return null;
			}
		}
		int size = list.size();
		List<ID123> rebooklist = Lists.newArrayList();
		if (1 >= start) {
			if (size <= PAGE_SIZE) {
				rebooklist = list;
			} else {
				rebooklist = list.subList(0, PAGE_SIZE);
			}
		} else {
			rebooklist = list.subList(start - 1, (start + PAGE_SIZE) > size ? size : start + PAGE_SIZE - 1);
		}
		return getBookVoList(rebooklist);
	}

	/**
	 * @param start
	 * @return 最新列表是正序查询 不断追加到redis中list的尾部
	 */
	public List<BookVo> queryNewest(Integer start) {
		List<ID123> list = BOOKLISTCACHE.getIfPresent(NewestBook_List);
		if (CollectionUtils.isEmpty(list)) {
			list = redisService.getListRage(NewestBook_List, 200l, ID123.class);
			if (CollectionUtils.isEmpty(list)) {
				intiNewestBook();
				return null;
			} else {
				BOOKLISTCACHE.put(NewestBook_List, list);
			}
		}
		int size = list.size();
		List<ID123> rebooklist = Lists.newArrayList();
		if (1 >= start) {
			if (size <= PAGE_SIZE) {
				rebooklist = list;
			} else {
				rebooklist = list.subList(0, PAGE_SIZE);
			}
		} else {
			if (start > size - 9) {
				rebooklist = redisService.getListRageFreedom(NewestBook_List, -(start + PAGE_SIZE.longValue()),
						-start.longValue(), ID123.class);
			} else {
				rebooklist = list.subList(start - 1, start + PAGE_SIZE - 1);
			}
		}
		return getBookVoList(rebooklist);
	}

	private List<BookVo> getBookVoList(List<ID123> rebooklist) {
		List<BookVo> relist = new ArrayList<BookVo>(rebooklist.size());
		for (final ID123 id123 : rebooklist) {
			Book bk = null;
			try {
				bk = book4096.get(id123.getBkid().toString(), new Callable<Book>() {
					@Override
					public Book call() throws Exception {
						return getBookById(id123.getBkid());
					}
				});
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			BookVo bkv = covent(bk);
			Book bk1 = null;
			Book bk2 = null;
			if (-1 != id123.getId23()[0]) {
				try {
					bk1 = book4096.get(id123.getId23()[0].toString(), new Callable<Book>() {
						@Override
						public Book call() throws Exception {
							return getBookById(id123.getId23()[0]);
						}
					});
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			if (-1 != id123.getId23()[1]) {
				try {
					bk2 = book4096.get(id123.getId23()[1].toString(), new Callable<Book>() {
						@Override
						public Book call() throws Exception {
							return getBookById(id123.getId23()[1]);
						}
					});
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			bkv.setNearlist(Lists.newArrayList(null==bk1?null:covent(bk1), null==bk2?null:covent(bk2)));
			relist.add(bkv);
		}
		return relist;
	}

	private Book getBookById(Long id) {
		Book bk = bookredisService.hGet(RedisService.BOOK_KEY, id.toString());
		if (null == bk) {
			bk = bookMapper.selectByPrimaryKey(id);
			bookredisService.hSet(RedisService.BOOK_KEY, id.toString(), bk);
		}
		return bk;
	}

	/**
	 * @param book
	 * @return 保存日记 同时放缓存
	 */
	public Long saveBook(Book book) {
		book.setCreatetime(System.currentTimeMillis());
		book.setUpdatetime(book.getCreatetime());
		book.setIsdel(0);
		int i = bookMapper.insertSelective(book);
		if (i > 0) {
			bookredisService.hSet(RedisService.BOOK_KEY, book.getId().toString(), book);
			longredisService.add2List(UserBook_List_ALL + book.getUserid(), book.getId());
			if (book.getIsopen() == 0) {
				longredisService.add2List(UserBook_List + book.getUserid(), book.getId());
			}
			return book.getId();
		} else {
			return -1l;
		}
	}

	/**
	 * @param userid
	 * @return 获取自己最近三条
	 */
	public List<BookVo> getBookVo123(Long userid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userid);
		map.put("page", 0);
		map.put("limit", 3);
		map.put("orderbystr", "createtime desc ");
		List<Book> lb = bookMapper.getBookList(map);
		if (null == lb || lb.size() == 0) {
			return null;
		}
		return covent(lb);
	}

	public int getMineTotal(long userid) {
		int total = bookMapper.getMineTotal(userid);
		return total;
	}

	public List<BookVo> getMine(long userid, Page page) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userid);
		map.put("page", page.getPage());
		map.put("limit", page.getLimit());
		map.put("orderbystr", "createtime desc ");
		List<Book> booklis = bookMapper.getBookList(map);
		return covent(booklis);
	}

	public List<Long> getMineBookIds(Long userid) {
		List<Long> minebooklis = new ArrayList<Long>();
		String key = UserBook_List + userid;
		if (redisService.exist(key)) {
			minebooklis = longredisService.getList(key, Long.class);
		} else {
			minebooklis = bookMapper.getMineBookId(userid);
			if (null != minebooklis && minebooklis.size() > 0) {
				longredisService.addAll2List(key, minebooklis);
			}
		}
		return minebooklis;
	}

	private List<BookVo> covent(List<Book> bl) {
		List<BookVo> bvl = new ArrayList<BookVo>();
		for (Book book : bl) {
			bvl.add(covent(book));
		}
		return bvl;
	}

	private BookVo covent(Book book) {
		BookVo bkv = new BookVo();
		try {
			BeanUtils.copyProperties(bkv, book);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		bkv.setMarkdate(df.format(new Date(bkv.getMarktime())));
		bkv.setCreatdate(df.format(new Date(bkv.getCreatetime())));
		bkv.setUpdatedate(df.format(new Date(bkv.getUpdatetime())));
		return bkv;
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	private Set<String> foreachGetId(List<ID123> id123list) {
		Set<String> ls = Sets.newHashSet();
		for (ID123 id123 : id123list) {
			ls.add(id123.getBkid().toString());
			if (-1 != id123.getId23()[0]) {
				ls.add(id123.getId23()[0].toString());
			}
			if (-1 != id123.getId23()[1]) {
				ls.add(id123.getId23()[1].toString());
			}
		}
		return ls;
	}

}
