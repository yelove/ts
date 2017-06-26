/**
 * 
 */
package com.ts.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author zsy 核心定时任务 用于不断刷新 各种日记 列表 从数据库拉取 放入缓存
 *
 */
@Service
public class TaskSevice {

	@Autowired
	BookService bookService;
	
	@Autowired
	MissionService missionService;

	/**
	 * 从数据库拉取最新日记ID列表
	 */
	@Scheduled(cron = "5 */2 * * * ?")
	public void newestBook() {
		bookService.intiNewestBook();
	}

	@Scheduled(cron = "15 */5 * * * ?")
	public void hotBook24H() {
		bookService.initHotBook(1);
		bookService.initHotBook(7);
	}

	@Scheduled(cron = "1 */20 * * * ?")
	public void hotBook30D() {
		bookService.initHotBook(30);
	}

	@Scheduled(cron = "3 3 */3 * * ?")
	public void hotBook1Y() {
		bookService.initHotBook(365);
	}

	@Scheduled(cron = "7 7 */12 * * ?")
	public void hotBookAll() {
		bookService.initHotBook(0);
	}
	
	@Scheduled(cron = "2 1 0 * * ?")
	public void todayMissionList() {
		missionService.doresetTodayMission();
	}

}
