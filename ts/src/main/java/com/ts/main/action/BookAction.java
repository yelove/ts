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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ts.main.bean.model.Book;
import com.ts.main.bean.model.User;
import com.ts.main.bean.vo.BookVo;
import com.ts.main.bean.vo.Page;
import com.ts.main.common.CommonStr;
import com.ts.main.service.BookService;

/**
 * @author hasee
 *
 */
@RequestMapping(value = "book/")
@Controller
public class BookAction {
	
	@Autowired
	private BookService bookService;
	
	/**
	 * @param mdmap
	 * @param bkv
	 * @param request
	 * @return 发表
	 */
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> addBook(ModelMap mdmap,BookVo bkv,HttpServletRequest request){
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if(null==obj){
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		Book bk = new Book();
		bk.setIsopen(bkv.getIsopen());
		bk.setUserid(((User)obj).getId());
		bk.setText(bkv.getText());
		if(StringUtils.isEmpty(bkv.getMarkdate())){
			bk.setMarktime(new Date().getTime());
		}else{
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
		if(id>0){
			rm.put(CommonStr.STATUS, 1000);
		}else{
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
	public @ResponseBody Map<String, Object> getBook123(ModelMap mdmap,HttpServletRequest request){
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if(null==obj){
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		List<BookVo> bvl = bookService.getBookVo123(((User)obj).getId());
		if(null!=bvl){
			rm.put(CommonStr.STATUS, 1000);
			rm.put("book123", bvl);
		}else{
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
	public @ResponseBody Map<String, Object> getMine(ModelMap mdmap,Page page,HttpServletRequest request){
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if(null==obj){
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		int total = bookService.getMineTotal(((User)obj).getId());
		List<BookVo> bvl = bookService.getMine(((User)obj).getId(),page);
		if(null!=bvl){
			rm.put(CommonStr.STATUS, 1000);
			rm.put("bookmine", bvl);
			rm.put("total", total);
		}else{
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}
	
	/**
	 * @param mdmap
	 * @param page
	 * @param request
	 * @return 获取热评文章列表
	 */
	@RequestMapping(value = "gethot", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getHotBooks(ModelMap mdmap,Page page,HttpServletRequest request){
		Map<String, Object> rm = new HashMap<String, Object>();
		List<BookVo> bvl = bookService.getHotBooks(page);
		if(null!=bvl){
			rm.put(CommonStr.STATUS, 1000);
			rm.put("bookhot", bvl);
		}else{
			rm.put(CommonStr.STATUS, 1002);
		}
		return rm;
	}

}
