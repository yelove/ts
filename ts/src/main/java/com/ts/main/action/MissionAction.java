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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ts.main.bean.Pager;
import com.ts.main.bean.model.Book;
import com.ts.main.bean.model.Mission;
import com.ts.main.bean.model.User;
import com.ts.main.bean.model.UserMission;
import com.ts.main.bean.vo.BookVo;
import com.ts.main.common.CommonStr;
import com.ts.main.service.BookService;
import com.ts.main.service.MissionService;
import com.ts.main.utils.TimeUtils4book;

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
	public @ResponseBody Map<String, Object> getMission(HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		List<Mission> mislis = misService.dogetTodayMission();
		if (null != mislis && mislis.size() > 0) {
			List<Mission> rmlis = Lists.newArrayList();
			rm.put(CommonStr.STATUS, 1000);
			rmlis.addAll(mislis);
			Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
			if (null != obj) {
				Long userid = ((User) obj).getId();
				List<UserMission> umlis = misService.getUserTodayMission(userid);
				if (!CollectionUtils.isEmpty(umlis)) {
					Map<Long, Object> mp = Maps.newHashMap();
					for (UserMission um : umlis) {
						mp.put(um.getMid(), null);
					}
					for (Mission mi : mislis) {
						if (mp.containsKey(mi.getId())) {
							mi.setMstatus(-1);
						}
					}
				}
				List<Mission> mislisod = misService.getOldeMissonByCache(userid);
				if (!CollectionUtils.isEmpty(mislisod)) {
					rmlis.addAll(mislisod);
				}
			}
			rm.put(CommonStr.DESC, rmlis);
			rm.put(CommonStr.LIT, mislis.size());
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	@RequestMapping(value = "finish/{mid}/{bid}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> finishMission(HttpServletRequest request, @PathVariable("mid") Long mid,
			@PathVariable("bid") Long bid) {
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == bid || bid.longValue() <= 0 || null == mid || mid.longValue() <= 0) {
			rm.put(CommonStr.STATUS, 1011);
			return rm;
		}
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		if (null != obj) {
			boolean flag = true;
			Long userid = ((User) obj).getId();
			rm.put(CommonStr.STATUS, 1000);
			List<Book> bl = bookService.getMineTodayBooks(userid);
			if (null != bl && bl.size() > 0) {
				for (Book bk : bl) {
					if (bid.longValue() == bk.getId().longValue()) {
						flag = false;
					}
				}
			}
			if (!flag) {
				int i = misService.doMission(mid, bid, userid);
				if (i < 1) {
					rm.put(CommonStr.STATUS, 1007);
				}
			} else {
				rm.put(CommonStr.STATUS, 1002);
			}
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}


	@RequestMapping(value = "submitfinish", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> submitfinishMission(HttpServletRequest request,BookVo bkv,@RequestParam(value = "mid", required = false,defaultValue = "-1")Long mid) {
		Map<String, Object> rm = new HashMap<String, Object>();
		if (StringUtils.isEmpty(bkv.getText())|| null == mid || mid <= 0) {
			rm.put(CommonStr.STATUS, 1011);
			return rm;
		}
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		if (null != obj) {
			boolean flag = true;
			Long userid = ((User) obj).getId();
			List<Mission> mislis = misService.dogetTodayMission();
			for(Mission misd : mislis){
				if(mid.equals(misd.getId())){
					flag = false;
					break;
				}
			}
			if(flag){
				mislis= misService.getOldeMissonByCache(userid);
				for(Mission misd : mislis){
					if(mid.equals(misd.getId())){
						flag = false;
						break;
					}
				}
			}
			if(flag){
				rm.put(CommonStr.STATUS, 1007);
				return rm;
			}
			Book bk = new Book();
			bk.setIsopen(0 == bkv.getIsopen() ? 0 : -1);
			bk.setUserid(userid);
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
					rm.put(CommonStr.STATUS, 1011);
					return rm;
				}
				bk.setMarktime(markdate.getTime());
			}
			Long bid = bookService.saveBook(bk);
			if(bid<1){
				rm.put(CommonStr.STATUS, 1020);
				return rm;
			}
			int i = misService.doMission(mid, bid, userid);
			if (i < 1) {
				rm.put(CommonStr.STATUS, 1020);
			}
			rm.put(CommonStr.STATUS, 1000);
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
			boolean flag = true;
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
			if (!flag) {
				rm.put(CommonStr.STATUS, 1004);
				return rm;
			}
			List<Book> bl = bookService.getMineTodayBooks(userid);
			if (null != bl && bl.size() > 0) {
				int xd = bl.size();
				for (Book bk : bl) {
					// 如果该日记已经被完整任务 则标记为置9
					if (ummap.containsKey(bk.getId())) {
						bk.setIsdel(9);
						xd--;
					}
				}
				if (xd == 0) {
					rm.put(CommonStr.STATUS, 1002);
				} else {
					rm.put(CommonStr.DESC, bl);
				}
			} else {
				rm.put(CommonStr.STATUS, 1002);
			}
			if (null != tp && tp.intValue() > 0) {
				return rm;
			}
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> doMission(HttpServletRequest request,
			@RequestParam(value = "ctpg", required = true) Integer ctpg,
			@RequestParam(value = "qstr", required = false) String qstr,
			@RequestParam(value = "stime", required = false) String stime,
			@RequestParam(value = "etime", required = false) String etime) {
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == ctpg) {
			rm.put(CommonStr.STATUS, 1007);
			return rm;
		}
		Pager<Mission> mispag = misService.queryMissionPage(ctpg, qstr, stime, etime);
		rm.put(CommonStr.STATUS, 1000);
		rm.put("orderlist", mispag.getReList());
		rm.put("ctpg", mispag.getCurrentPage());
		rm.put("maxpg", mispag.getTotalRows());
		rm.put("maxrow", mispag.getTotalSize());
		return rm;
	}

	@RequestMapping(value = "domission/{mid}/{bid}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> queryMission(HttpServletRequest request, @PathVariable("mid") Long mid,
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
	
	@RequestMapping(value = "getall", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> queryAllMission(
			@RequestParam(value = "page", required = false,defaultValue = "1") Integer page) {
		Map<String, Object> rm = new HashMap<String, Object>();
		List<Mission> mx = misService.getMissionListByPage(page);
		rm.put(CommonStr.TOTALSIZE, misService.getAllMission().size());
		rm.put(CommonStr.RESULT, mx);
		rm.put(CommonStr.STATUS, 1000);
		return rm;
	}
	
	@RequestMapping(value = "getone", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> queryOneMission(
			@RequestParam(value = "id", required = false,defaultValue = "0") Long id,
			@RequestParam(value = "page", required = false,defaultValue = "1") Integer page) {
		Map<String, Object> rm = new HashMap<String, Object>();
		Mission mis = misService.getAllMissionById(id);
		if(null==mis){
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		rm.put(CommonStr.DESC, mis);
		Pager<BookVo> mx = bookService.getBookByMid(id,page);
		if(null==mx){
			rm.put(CommonStr.STATUS, 1002);
		}else{
			rm.put(CommonStr.TOTALSIZE, mx.getTotalSize());
			rm.put(CommonStr.RESULT, mx.getReList());
			rm.put(CommonStr.STATUS, 1000);
		}
		return rm;
	}

	@RequestMapping(value = "add", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> addMission(HttpServletRequest request,
			@RequestParam(value = "mgs", required = true) String mgs,
			@RequestParam(value = "stime", required = true) String stime,
			@RequestParam(value = "etime", required = false) String etime,
			@RequestParam(value = "mtype", required = false) String mtype) {
		Map<String, Object> rm = new HashMap<String, Object>();
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		if (null != obj) {
			User user = (User) obj;
			Mission mis = new Mission();
			mis.setCreator(user.getEmail());
			mis.setEnddate(TimeUtils4book.str2date(StringUtils.isEmpty(etime)?stime:etime, TimeUtils4book.yMd_).getTime()+24*3600000l-1);
			mis.setStartdate(TimeUtils4book.str2date(stime, TimeUtils4book.yMd_).getTime());
			mis.setMission(mgs);
			mis.setMtype(mtype);
			mis.setRank(1);
			int i = misService.saveMission(mis);
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
