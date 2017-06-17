/**
 * 
 */
package com.ts.main.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ts.main.bean.model.Mission;
import com.ts.main.bean.model.UserMission;
import com.ts.main.mapper.MissionMapper;
import com.ts.main.mapper.UserMissionMapper;
import com.ts.main.utils.TimeUtils4book;

/**
 * @author zsy
 *
 */
@Service
public class MissionService {
	
	@Autowired
	private MissionMapper missionMapper;
	
	@Autowired
	private UserMissionMapper umMapper;
	
	private static List<String> missionTmp = new ArrayList<String>();
	
	public static final String DAILY = "daily";
	
	public static final String WEEK = "week";
	
	static{
		missionTmp.add("去年的这个时候,你在哪里,做些什么.");
		missionTmp.add("曾经的同学,现在的朋友们都还有联系么.");
		missionTmp.add("今天的天气是否会影响你的心情.");
		missionTmp.add("曾经给自己定的小目标实现了嘛?");
		missionTmp.add("曾经的理想是否都败给现实.");
	}
	
	private static List<Mission> toDayMission;
	
	public List<Mission> dogetTodayMission(){
		if(null==toDayMission||toDayMission.isEmpty()){
			toDayMission = missionMapper.selectMissionByDate(System.currentTimeMillis());
			if(null==toDayMission||toDayMission.isEmpty()){
				Random random = new Random();
				Mission mis = new Mission();
				mis.setMission(missionTmp.get(random.nextInt(5)));
				mis.setMstatus(0);
				mis.setMtype(DAILY);
				mis.setCreator("system");
				mis.setRank(1);
				Long dayfirts = TimeUtils4book.getTimesmorning();
				mis.setStartdate(dayfirts);
				mis.setEnddate(dayfirts+(24*3600000)-1);
				saveMission(mis);
				toDayMission = missionMapper.selectMissionByDate(System.currentTimeMillis());
			}
		}
		return toDayMission;
	}
	
	
	public int saveMission(Mission mis){
		Long nowdt = System.currentTimeMillis();
		mis.setCreatetime(nowdt);
		mis.setUpdatetime(nowdt);
		return missionMapper.insert(mis);
	}
	
	public List<UserMission> getUserMission(Long userid){
		return umMapper.selectByUserId(userid);
	}
	
	public List<UserMission> getUserTodayMission(Long userid){
		return umMapper.selectByUserIdAndDate(userid, System.currentTimeMillis(),TimeUtils4book.getTimesmorning());
	}
	
	public int saveUserMission(UserMission um){
		Long nowdt = System.currentTimeMillis();
		um.setCreatetime(nowdt);
		um.setUpdatetime(nowdt);
		um.setUmstatus(0);;
		return umMapper.insert(um);
	}
	
	public static void main(String args[]){
		System.out.println(TimeUtils4book.getTimesmorning());
		System.out.println(System.currentTimeMillis());
		System.out.println(TimeUtils4book.getTimesmorning()+24*3600*1000);
//		for(int i = 0;i<20;i++){
//			Random random = new Random();
//			System.out.println(random.nextInt(5));
//		}
		System.out.println(new Date(TimeUtils4book.getTimesmorning()));
		System.out.println(new Date(TimeUtils4book.getTimesmorning()+24*3600*1000));
	}


	public int doMission(Long mid, Long bid,Long uid) {
		UserMission um = new UserMission();
		um.setBkid(bid);
		um.setUid(uid);
		um.setMid(mid);
		um.setUmstatus(0);
		um.setCreatetime(System.currentTimeMillis());
		um.setUpdatetime(System.currentTimeMillis());
		return umMapper.insert(um);
	}


}
