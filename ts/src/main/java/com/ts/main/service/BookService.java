/**
 * 
 */
package com.ts.main.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ts.main.bean.Pager;
import com.ts.main.bean.model.Book;
import com.ts.main.bean.model.BookZan;
import com.ts.main.bean.model.User;
import com.ts.main.bean.vo.BookVo;
import com.ts.main.bean.vo.Page;
import com.ts.main.common.CommonStr;
import com.ts.main.mapper.BookMapper;
import com.ts.main.mapper.BookZanMapper;
import com.ts.main.utils.TimeUtils4book;

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

	// public static final String UserBook_List_ALL = "UserBook_List_All_";
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
	BookZanMapper bookZanMapper;

	@Autowired
	UserService userService;
	
	@Autowired
	RedisService<Book> bookredisService;

	@Autowired
	RedisService<Long> longredisService;
	/**
	 * 本地缓存 热评列表 由于定时任务会在每台机器上执行并刷新 所以可以用本地缓存而不用redis
	 * 并且每个列表仅缓存200条数据对内存也无压力
	 */
	private static Cache<String,List<Long>> booklist = CacheBuilder.newBuilder()
			.expireAfterWrite(1, TimeUnit.HOURS).softValues().initialCapacity(8).build();

	/**
	 * 初始化最新列表 并取200条缓存到本地
	 */
	public void intiNewestBook() {
		if (longredisService.setNx("Task_newestBook", 3)) {
			List<Long> booklis = null;
			boolean flag = false;
			Long bigid;
			if (longredisService.exist(NewestBook_List)) {
				// redis中存在缓存则 已最后一个ID 为起始值查询 即 lrange get0
				bigid = longredisService.getListFirst(NewestBook_List, Long.class);
				//以缓存中的最大id为基准id从数据库中查询比
				booklis = bookMapper.getNewestBook(bigid);
			} else {
				// 初始化 最新 列表
				booklis = bookMapper.getAllBookIdAndUserId();
				flag = true;
			}
			if (null == booklis || booklis.size() == 0) {
				if(!flag){
					booklist.put(NewestBook_List, longredisService.getLisetinner(NewestBook_List, 0l, 200l, Long.class));
				}
				return;
			}
			if (flag) {
				//全量的id输入需要 rightpush 即 id从大到小 从右边依次添加入list保证 lrange get0是最大值
				longredisService.addAll2List(NewestBook_List, booklis);
			} else {
				//部分追加 则需要从大到小
				longredisService.addAll2ListLeft(NewestBook_List, booklis);
			}
			booklist.put(NewestBook_List, longredisService.getLisetinner(NewestBook_List, 0l, 200l, Long.class));
		}
	}

	public void initHotBook(int day) {
			List<Long> booklis = bookMapper
					.getHotBook(day == 0 ? 0l : TimeUtils4book.getBefore(new Long(day), TimeUnit.DAYS));
			if (null == booklis || booklis.size() == 0) {
				return;
			}
			switch (day) {
			case 1:
				booklist.put(HotBook24H, booklis);
				break;
			case 7:
				booklist.put(HotBook7D, booklis);
				break;
			case 30:
				booklist.put(HotBook30D, booklis);
				break;
			case 365:
				booklist.put(HotBook1Y, booklis);
				break;
			case 0:
				booklist.put(HotBookAll, booklis);
				break;
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
			fkey = HotBook24H;
			break;
		case 7:
			fkey = HotBook7D;
			break;
		case 30:
			fkey = HotBook30D;
			break;
		case 365:
			fkey = HotBook1Y;
			break;
		case 0:
			fkey = HotBookAll;
			break;
		}
		List<Long> list = booklist.getIfPresent(fkey);
		if (null == list) {
			initHotBook(day);
			list = booklist.getIfPresent(fkey);
			if (null == list) {
				return null;
			}
		}
		int size = list.size();
		List<Long> rebooklist = Lists.newArrayList();
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
		List<Long> list = booklist.getIfPresent(NewestBook_List);
		if (CollectionUtils.isEmpty(list)) {
			list = longredisService.getListRage(NewestBook_List, 200l, Long.class);
			if (CollectionUtils.isEmpty(list)) {
				intiNewestBook();
				list = booklist.getIfPresent(NewestBook_List);
				if(CollectionUtils.isEmpty(list)){
					return null;
				}
			} else {
				booklist.put(NewestBook_List, list);
			}
		}
		int size = list.size();
		List<Long> rebooklist = Lists.newArrayList();
		if (1 >= start) {
			if (size <= PAGE_SIZE) {
				rebooklist = list;
			} else {
				rebooklist = list.subList(0, PAGE_SIZE);
			}
		} else {
			if (start > size) {
				rebooklist = longredisService.getListRageFreedom(NewestBook_List, start.longValue() - 1,
						start.longValue() + PAGE_SIZE, Long.class);
			} else {
				rebooklist = list.subList(start - 1, (start + PAGE_SIZE) >= size ? size - 1 : start + PAGE_SIZE - 1);
			}
		}
		return getBookVoList(rebooklist);
	}

	private List<BookVo> getBookVoList(List<Long> rebooklist) {
		List<BookVo> relist = new ArrayList<BookVo>(rebooklist.size());
		Map<Long,Book> bookmap = getBookBatch(rebooklist);
		for (Long id : rebooklist) {
			Book bk = bookmap.get(id);
			BookVo bkv = covent(bk);
			relist.add(bkv);
		}
		return relist;
	}

	public Book getBookById(Long id) {
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
			if (book.getIsopen() == 0) {
				longredisService.add2ListLeft(UserBook_List + book.getUserid(), book.getId());
				longredisService.add2ListLeft(NewestBook_List, book.getId());
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

	public Page getView(long userid, int start) {
		List<Long> viewBookidlis = getMineBookIds(userid);
		Page page = new Page();
		page.setTotalRows(new Long(viewBookidlis.size()));
		if (CollectionUtils.isEmpty(viewBookidlis)) {
			return page;
		}
		page.setTotalRows(new Long(viewBookidlis.size()));
		List<Long> pageidlis = viewBookidlis.subList(start - 1,
				start + PAGE_SIZE > viewBookidlis.size() ? viewBookidlis.size(): start + PAGE_SIZE);
		List<BookVo> bvolis = Lists.newArrayList();
		Map<Long,Book> bkmap = getBookBatch(pageidlis);
		for (final Long id : pageidlis) {
			Book bk = bkmap.get(id);
			if (null == bk) {
				continue;
			}
			bvolis.add(covent(bk));
		}
		page.setList(bvolis);
		return page;
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
		if (longredisService.exist(key)) {
			minebooklis = longredisService.getList(key, Long.class);
		} else {
			minebooklis = bookMapper.getMineBookId(userid);
			if (null != minebooklis && minebooklis.size() > 0) {
				longredisService.addAll2List(key, minebooklis);
			}
		}
		return minebooklis;
	}
	
	public List<Book> getMineTodayBooks(Long userid){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userid);
		map.put("createtime", TimeUtils4book.getTimesmorning());
		return bookMapper.getBookList(map);
	}

	private List<BookVo> covent(List<Book> bl) {
		List<BookVo> bvl = new ArrayList<BookVo>();
		for (Book book : bl) {
			bvl.add(covent(book));
		}
		return bvl;
	}

	private BookVo covent(Book book) {
		if (null == book) {
			return null;
		}
		BookVo bkv = new BookVo();
		try {
			BeanUtils.copyProperties(bkv, book);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		bkv.setMarkdate(TimeUtils4book.date2str(new Date(bkv.getMarktime())));
		bkv.setCreatdate(TimeUtils4book.date2str(new Date(bkv.getCreatetime())));
		bkv.setUpdatedate(TimeUtils4book.date2str(new Date(bkv.getUpdatetime())));
		bkv.setInterval(TimeUtils4book.dateInterval(bkv.getCreatetime()));
		User user = userService.getUserBiIdWithCache(book.getUserid());
		bkv.setTsno(user.getTsno());
		if (!StringUtils.isEmpty(user.getImgurl())) {
			bkv.setUserImgUrl(user.getImgurl());
		}
		return bkv;
	}

	@SuppressWarnings("unused")
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
	
	public Map<Long,Book> getBookBatch(List<Long> ids){
		Map<Long,Book> hdbookmap = Maps.newHashMap();
		Set<String> idset = Sets.newHashSet();
		for(Long id : ids){
			idset.add(String.valueOf(id));
		}
		if(!idset.isEmpty()){
			List<Object> olis = bookredisService.hGetList(RedisService.BOOK_KEY, idset);
			if(!CollectionUtils.isEmpty(olis)){
				for(Object obj : olis){
					if(null!=obj){
						Book bk = (Book)obj;
						hdbookmap.put(bk.getId(), bk);
						idset.remove(String.valueOf(bk.getId()));
					}
				}
			}
			if(!idset.isEmpty()){
				Map<String,Object> map = Maps.newHashMap();
				map.put("ids", Lists.newArrayList(idset));
				List<Book> bklis = bookMapper.getBookList(map);
				Map<String,Book> xtmap = Maps.newHashMap();
				for(Book bk : bklis){
					hdbookmap.put(bk.getId(), bk);
					xtmap.put(String.valueOf(bk.getId()), bk);
				}
				bookredisService.hSetAll(RedisService.BOOK_KEY, xtmap);
			}
		}
		return hdbookmap;
	}

	public int update(Book bknew) {
		int i = bookMapper.updateByPrimaryKeySelective(bknew);
		if (i > 0) {
			bookredisService.hSet(RedisService.BOOK_KEY, bknew.getId().toString(), bknew);
		}
		return i;
	}

	public void updateBookCommentSize(Long bookid, boolean flag) {
		if (flag) {
			bookMapper.updateBookCommentSize(bookid);
		} else {
			bookMapper.delBookCommentSize(bookid);
		}
		reCacheBook(bookid);
	}

	private void reCacheBook(Long bookid) {
		Book bk = bookMapper.selectByPrimaryKey(bookid);
		if (null != bk) {
			bookredisService.hSet(RedisService.BOOK_KEY, bookid.toString(), bk);
		}
	}
	
	private static final String BOOKZAN = "bkzan_";

	/**
	 * 给book点赞
	 * 必须确保 bookid存在 
	 * @param userId
	 * @param bookId
	 * @return
	 */
	public int bookZan(Long userId, Long bookId) {
		BookZan bzk = new BookZan();
		bzk.setBookid(bookId);
		bzk.setUserid(userId);
		if (longredisService.hHashKey(BOOKZAN+bookId, String.valueOf(userId))) {
			updateBookZan(bzk,false);
			longredisService.hDel(BOOKZAN+bookId, String.valueOf(userId));
			return -1;
		}else{
			BookZan bkzan = bookZanMapper.selectByPrimaryKey(bzk);
			if(null==bkzan){
				updateBookZan(bzk,true);
				longredisService.hSet(BOOKZAN+bookId, String.valueOf(userId),0l);
				return 1;
			}else{
				updateBookZan(bzk,false);
				//初始化
				List<Long> userid = bookZanMapper.selectByBookId(bzk.getBookid());
				Map<String,Long> map = Maps.newHashMap();
				for(Long uid : userid){
					map.put(String.valueOf(uid), 1l);
				}
				longredisService.hSetAll(BOOKZAN+bookId, map);
				return -1;
			}
		}
	}
	
	/**
	 * @param bookId
	 * @param userId
	 * @param flag
	 */
	@Transactional
	public void updateBookZan(BookZan bzk,boolean flag){
		if(flag){
			bzk.setCreatetime(System.currentTimeMillis());
			if(bookZanMapper.insert(bzk)>0)
				bookMapper.updateBookZan(bzk.getBookid(),1);
		}else{
			if(bookZanMapper.deleteByPrimaryKey(bzk)>0)
				bookMapper.updateBookZan(bzk.getBookid(),-1);
			
		}
		reCacheBook(bzk.getBookid());
	}
	
	public static final String MISSIONBOOK = "misbk_";
	
	public Pager<BookVo> getBookByMid(Long id,Integer page) {
		String key = MISSIONBOOK+id;
		if(!longredisService.exist(key)){
			List<Long> bkidlis  = bookMapper.getBookIdListByMid(id);
			if(CollectionUtils.isEmpty(bkidlis)){
				//无数据
				return null;
			}else{
				longredisService.addAll2List(key, bkidlis);
			}
		}
		int size = longredisService.getListSize(key).intValue();
		int xstart = (page-1)*CommonStr.BIGPAGESIZE;
		Pager<BookVo> bkpage  = new Pager<BookVo>();
		bkpage.setCurrentPage(page);
		bkpage.setPageSize(CommonStr.BIGPAGESIZE);
		int xmax = xstart+CommonStr.BIGPAGESIZE;
		if(size<=xmax){
			xmax = size;
		}
		List<Long> bkidlis = longredisService.getLisetinner(key, (long)xstart, (long)xmax, Long.class);
		List<BookVo> bklis = null==bkidlis?new ArrayList<BookVo>():getBookVoList(bkidlis);
		bkpage.setReList(bklis);
		bkpage.setTotalSize(size);
		return bkpage;
	}

}
