/**
 * 
 */
package com.ts.main.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.ts.main.bean.model.Book;
import com.ts.main.bean.model.Mission;
import com.ts.main.bean.model.User;
import com.ts.main.bean.model.UserMission;
import com.ts.main.common.CommonStr;
import com.ts.main.service.BookService;
import com.ts.main.service.MissionService;

/**
 * @author zsy
 *
 */
@Controller
@RequestMapping("mission")
public class MissionAction {

	@Autowired
	private MissionService misService;

	@Autowired
	private BookService bookService;

	@RequestMapping(value = "getmission", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getMission() {
		Map<String, Object> rm = new HashMap<String, Object>();
		List<Mission> mislis = misService.dogetTodayMission();
		if (null != mislis && mislis.size() > 0) {
			rm.put(CommonStr.STATUS, 1000);
			rm.put(CommonStr.DESC, mislis);
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}
	
	@RequestMapping(value = "finish/{mid}/{bid}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> finishMission(HttpServletRequest request, @PathVariable("mid") Long mid, @PathVariable("bid") Long bid) {
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == bid || bid.longValue() <= 0||null == mid || mid.longValue() <= 0) {
			rm.put(CommonStr.STATUS, 1011);
			return rm;
		}
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		if (null != obj) {
			List<Mission> mislis = misService.dogetTodayMission();
			boolean flag = true;
			for (Mission mis : mislis) {
				if (mis.getId().longValue() == mid.longValue()) {
					flag = false;
					break;
				}
			}
			// 任务ID不是当天的 直接返回
			if (flag) {
				rm.put(CommonStr.STATUS, 1007);
				return rm;
			}
			flag = true;
			Long userid = ((User) obj).getId();
			rm.put(CommonStr.STATUS, 1000);
			List<Book> bl = bookService.getMineTodayBooks(userid);
			if (null != bl && bl.size() > 0) {
				for (Book bk : bl) {
					if (bid.longValue()==bk.getId().longValue()) {
						flag = false;
					}
				}
			}
			if(!flag){
				int i = misService.doMission(mid,bid,userid);
				if(i<1){
					rm.put(CommonStr.STATUS, 1007);
				}
			}else{
				rm.put(CommonStr.STATUS, 1002);
			}
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	@RequestMapping(value = "check/{id}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> checkMission(HttpServletRequest request, @PathVariable("id") Long id,
			@RequestParam(value = "tp", required = false) Integer tp) {
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == id || id.longValue() <= 0) {
			rm.put(CommonStr.STATUS, 1011);
			return rm;
		}
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		if (null != obj) {
			List<Mission> mislis = misService.dogetTodayMission();
			boolean flag = true;
			for (Mission mis : mislis) {
				if (mis.getId().longValue() == id.longValue()) {
					flag = false;
					break;
				}
			}
			// 任务ID不是当天的 直接返回
			if (flag) {
				rm.put(CommonStr.STATUS, 1007);
				return rm;
			}
			flag = true;
			Long userid = ((User) obj).getId();
			rm.put(CommonStr.STATUS, 1000);
			// 拉取当前用户已完成的任务
			List<UserMission> umlis = misService.getUserTodayMission(userid);
			Map<Long, Long> ummap = Maps.newHashMap();
			if (null != umlis && umlis.size() > 0) {
				for (UserMission um : umlis) {
					if (um.getMid().longValue() == id.longValue()) {
						flag = false;
					}
					ummap.put(um.getBkid(), um.getMid());
				}
			}
			List<Book> bl = bookService.getMineTodayBooks(userid);
			if (null != bl && bl.size() > 0) {
				for (Book bk : bl) {
					// 如果该日记已经被完整任务 则标记为置9
					if (ummap.containsKey(bk.getId())) {
						bk.setIsdel(9);
					}
				}
				rm.put(CommonStr.DESC, bl);
			} else {
				rm.put(CommonStr.STATUS, 1002);
			}
			if (null != tp && tp.intValue() > 0) {
				return rm;
			}
			if (!flag) {
				rm.put(CommonStr.STATUS, 1004);
			}
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	@RequestMapping(value = "domission/{mid}/{bid}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> doMission(HttpServletRequest request, @PathVariable("mid") Long mid,
			@PathVariable("bid") Long bid) {
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == mid || mid.longValue() <= 0 || null == bid || bid.longValue() <= 0) {
			rm.put(CommonStr.STATUS, 1007);
			return rm;
		}
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		if (null != obj) {
			Long userid = ((User) obj).getId();
			int i = misService.doMission(mid, bid, userid);
			if (i > 0) {
				rm.put(CommonStr.STATUS, 1000);
			} else {
				rm.put(CommonStr.STATUS, 1007);
			}
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

}
