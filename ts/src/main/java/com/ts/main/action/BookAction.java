/**
 * 
 */
package com.ts.main.action;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ts.main.bean.model.Book;
import com.ts.main.bean.model.Comment;
import com.ts.main.bean.model.User;
import com.ts.main.bean.vo.BookVo;
import com.ts.main.bean.vo.CommentVo;
import com.ts.main.bean.vo.Page;
import com.ts.main.common.CommonStr;
import com.ts.main.service.BookService;
import com.ts.main.service.CommentService;
import com.ts.main.service.UserService;

/**
 * @author hasee
 *
 */
@RequestMapping(value = "book/")
@Controller
public class BookAction {

	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommentService commentService;

	/**
	 * @param mdmap
	 * @param bkv
	 * @param request
	 * @return 发表
	 */
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> addBook(ModelMap mdmap, BookVo bkv, HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == obj) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		Book bk = new Book();
		bk.setIsopen(0 == bkv.getIsopen() ? 0 : -1);
		bk.setUserid(((User) obj).getId());
		if (StringUtils.isEmpty(bkv.getText()) || bkv.getText().length() > 8000) {
			rm.put(CommonStr.STATUS, 1005);
			return rm;
		}
		bk.setText(bkv.getText());
		if (StringUtils.isEmpty(bkv.getMarkdate())) {
			bk.setMarktime(new Date().getTime());
		} else {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date markdate = null;
			try {
				markdate = df.parse(bkv.getMarkdate());
			} catch (ParseException e) {
				e.printStackTrace();
				rm.put(CommonStr.STATUS, 1009);
				return rm;
			}
			bk.setMarktime(markdate.getTime());
		}
		Long id = bookService.saveBook(bk);
		if (id > 0) {
			rm.put(CommonStr.STATUS, 1000);
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	@RequestMapping(value = "edit/{id}", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> editBook(
			@RequestParam(value = "txt", required = true, defaultValue = "") String txt, @PathVariable("id") Long id,
			HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == obj || StringUtils.isEmpty(txt)) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		Book bk = bookService.getBook(id);
		if (null == bk || bk.getUserid().longValue() != ((User) obj).getId().longValue()) {
			rm.put(CommonStr.STATUS, 1006);
			return rm;
		}
		bk.setText(txt);
		bk.setUpdatetime(System.currentTimeMillis());
		int i = bookService.update(bk);
		if (i > 0) {
			rm.put(CommonStr.STATUS, 1000);
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	/**
	 * @param mdmap
	 * @param bkv
	 * @param request
	 * @return 查询自己最近三条
	 */
	@RequestMapping(value = "get123", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getBook123(ModelMap mdmap, HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == obj) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		List<BookVo> bvl = bookService.getBookVo123(((User) obj).getId());
		if (null != bvl) {
			rm.put(CommonStr.STATUS, 1000);
			rm.put("book123", bvl);
		} else {
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}

	/**
	 * @param mdmap
	 * @param bkv
	 * @param request
	 * @return 查询自己所有的文章
	 */
	@RequestMapping(value = "getmine", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getMine(ModelMap mdmap, Page page, HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == obj) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		int total = bookService.getMineTotal(((User) obj).getId());
		List<BookVo> bvl = bookService.getMine(((User) obj).getId(), page);
		if (null != bvl) {
			rm.put(CommonStr.STATUS, 1000);
			rm.put("bookmine", bvl);
			rm.put("total", total);
		} else {
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}

	/**
	 * @param mdmap
	 * @param bkv
	 * @param request
	 * @return 访问别人的主页
	 */
	@RequestMapping(value = "getview", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> getView(
			@RequestParam(value = "start", required = true, defaultValue = "1") Integer start,
			@RequestParam(value = "uid", required = true, defaultValue = "0") Long uid) {
		Map<String, Object> rm = new HashMap<String, Object>();
		if (uid <= 0) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		User user = userService.getUserBiIdWithCache(uid);
		if (null == user) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		Page page = bookService.getView(uid, start);
		rm.put(CommonStr.STATUS, 1000);
		rm.put("bookview", page.getList());
		rm.put("username", user.getTsno());
		rm.put("total", page.getTotalRows());
		return rm;
	}

	/**
	 * @param mdmap
	 * @param page
	 * @param request
	 * @return 获取热评文章列表
	 */
	@RequestMapping(value = "hot24h", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getHot24H(
			@RequestParam(value = "start", required = true, defaultValue = "1") Integer start) {
		Map<String, Object> rm = new HashMap<String, Object>();
		List<BookVo> bvl = bookService.queryHotBook(1, start);
		if (null != bvl) {
			rm.put(CommonStr.STATUS, 1000);
			rm.put("bookhot", bvl);
		} else {
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}

	@RequestMapping(value = "hot7d", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getHot1d(
			@RequestParam(value = "start", required = true, defaultValue = "1") Integer start,
			HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		List<BookVo> bvl = bookService.queryHotBook(7, start);
		if (null != bvl) {
			rm.put(CommonStr.STATUS, 1000);
			rm.put("bookhot", bvl);
		} else {
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}

	@RequestMapping(value = "hot30d", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getHot30d(
			@RequestParam(value = "start", required = true, defaultValue = "1") Integer start,
			HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		List<BookVo> bvl = bookService.queryHotBook(30, start);
		if (null != bvl) {
			rm.put(CommonStr.STATUS, 1000);
			rm.put("bookhot", bvl);
		} else {
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}

	@RequestMapping(value = "hot365d", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getHot365d(
			@RequestParam(value = "start", required = true, defaultValue = "1") Integer start,
			HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		List<BookVo> bvl = bookService.queryHotBook(365, start);
		if (null != bvl) {
			rm.put(CommonStr.STATUS, 1000);
			rm.put("bookhot", bvl);
		} else {
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}

	@RequestMapping(value = "newest", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getNewest(
			@RequestParam(value = "start", required = true, defaultValue = "1") Integer start,
			HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		List<BookVo> bvl = bookService.queryNewest(start);
		if (null != bvl) {
			rm.put(CommonStr.STATUS, 1000);
			rm.put("bookhot", bvl);
		} else {
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}

	@RequestMapping(value = "subcomment", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> subComment(
			@RequestParam(value = "comment", required = true) String comment,
			@RequestParam(value = "bookid", required = true) Long bookid, HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == obj) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		if (StringUtils.isEmpty(comment) || null == bookid || bookid < 1) {
			rm.put(CommonStr.STATUS, 1006);
			return rm;
		}
		User user = (User) obj;
		Comment cmt = new Comment();
		cmt.setBookid(bookid);
		cmt.setComment(comment);
		cmt.setUserid(user.getId());
		if (commentService.saveComment(cmt) > 0) {
			rm.put(CommonStr.STATUS, 1000);
		} else {
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}

	@RequestMapping(value = "getcomment", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> getcomment(@RequestParam(value = "bookid", required = true) Long bookid,
			HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == bookid || bookid < 1) {
			rm.put(CommonStr.STATUS, 1006);
			return rm;
		}
		CommentVo vo = commentService.getCommentByBookId(bookid);
		if (null != vo) {
			rm.put("bkv", vo);
		}
		rm.put(CommonStr.STATUS, 1000);
		return rm;
	}

	@RequestMapping(value = "zanbook", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> zanbook(@RequestParam(value = "bookid", required = true) Long bookid,
			HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == obj) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		if (null == bookid || bookid < 1) {
			rm.put(CommonStr.STATUS, 1006);
			return rm;
		}
		User user = (User) obj;
		rm.put(CommonStr.STATUS, 1000);
		rm.put(CommonStr.CODE, bookService.bookZan(user.getId(), bookid));
		return rm;
	}

	@RequestMapping(value = "zancomment", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> zancomment(@RequestParam(value = "cmtid", required = true) Long cmtid,
			HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == obj) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		if (null == cmtid || cmtid < 1) {
			rm.put(CommonStr.STATUS, 1006);
			return rm;
		}
		User user = (User) obj;
		if (commentService.commentZan(user.getId(), cmtid) > 1) {
			rm.put(CommonStr.STATUS, 1000);
		} else {
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}

}
