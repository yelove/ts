/**
 * Copyright©2016 zsy. all rights reserved.
 *
 * @Title MsgService.java
 * @Prject bidding
 * @Package com.taika.bidding.service
 * @author 张绍云
 * @date 2016年6月17日 下午8:58:55
 * @Description  
 */
package com.ts.main.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ts.main.bean.Msg;
import com.ts.main.bean.Pager;
import com.ts.main.common.CommonStr;
import com.ts.main.dao.MsgDao;
import com.ts.main.util.TimeUtils;

/**
 * @ClassName MsgService
 * @Description
 * @author 张绍云
 * @date 2016年6月17日 下午8:58:55
 */
@Service
public class MsgService {

	@Autowired
	private MsgDao msgDao;

	private static List<Msg> msglist = new ArrayList<Msg>();

	private static String msgstr = "";

	private static String today = "";

	private long nextday = 0;

	private int size = 0;

	@PostConstruct
	public void intiMsgList() {
		List<Msg> lm = msgDao.getNewMsg();
		msglist.addAll(lm);
		createMsgStr();
		nextday = getNextDay();
		size = msgDao.countTodayMsg(TimeUtils.str2date(today, TimeUtils.yMd).getTime());
	}

	private long getNextDay() {
		today = getTody();
		return TimeUtils.str2date(today, TimeUtils.yMd).getTime() + 24 * 3600000;
	}

	private String getTody() {
		return TimeUtils.date2str(new Date(), TimeUtils.yMd);
	}

	private void createMsgStr() {
		msgstr = JSON.toJSONString(msglist);
	}

	public String getMsgStr() {
		return msgstr;
	}

	public List<Msg> getNewMsg() {
		return msglist;
	}

	public boolean saveMsg(Msg msg) {
		if (nextday != 0 && System.currentTimeMillis() > nextday) {
			nextday = getNextDay();
			size = 0;
		}
		String no = today.substring(2, today.length()) + getNext();
		msg.setNo(no);
		Long id = msgDao.saveMsg(msg);
		if (id > 0) {
			msg.setId(id);
			msglist.add(msg);
			createMsgStr();
		}
		return true;
	}

	public boolean updateMsg(Long id) {
		boolean flag = msgDao.updateMsg(id);
		if (flag) {
			for (int i = 0; i < msglist.size(); i++) {
				if (msglist.get(i).getId() == id) {
					msglist.remove(i);
					break;
				}
			}
			createMsgStr();
		}
		return flag;
	}

	public Pager<Msg> queryOldMsg(String qstr, int currentPage) {
		Pager<Msg> msgpager = new Pager<Msg>();
		msgpager.setCurrentPage(currentPage);
		msgpager.setTotalSize(msgDao.count(qstr));
		msgpager.setReList(msgDao.getOldMsg(currentPage, CommonStr.PAGESIZE, qstr));
		return msgpager;
	}

	private String getNext() {
		String rs = "";
		if (size < 0) {
			size = 0;
		}
		if (size < 10) {
			rs = "0000" + size;
			size = size + 1;
			return rs;
		}
		if (size < 100) {
			rs = "000" + size;
			size = size + 1;
			return rs;
		}
		if (size < 1000) {
			rs = "00" + size;
			size = size + 1;
			return rs;
		}
		if (size < 10000) {
			rs = "0" + size;
			size = size + 1;
			return rs;
		}
		if (size < 100000) {
			rs = "" + size;
			size = size + 1;
			return rs;
		}
		rs = "A" + size;
		size = size + 1;
		return rs;
	}

}
