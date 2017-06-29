/**
 * 
 */
package com.ts.main.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ts.main.bean.Pager;
import com.ts.main.bean.model.Mission;
import com.ts.main.bean.model.UserMission;
import com.ts.main.common.CommonStr;
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

	@Autowired
	RedisService<Long> longredisService;

	private static List<String> missionTmp = new ArrayList<String>();

	public static final String DAILY = "daily";

	public static final String WEEK = "week";

	static {
		missionTmp.add("去年的这个时候,你在哪里,做些什么.");
		missionTmp.add("曾经的同学,现在的朋友们都还有联系么.");
		missionTmp.add("今天的天气是否会影响你的心情.");
		missionTmp.add("曾经给自己定的小目标实现了嘛?");
		missionTmp.add("曾经的理想是否都败给现实.");
	}

	private static List<Mission> toDayMission;

	public List<Mission> dogetTodayMission() {
		if (null == toDayMission || toDayMission.isEmpty()) {
			doresetTodayMission();
		}
		return jsonClone(toDayMission);
	}

	public void doresetTodayMission() {
		if (longredisService.setNx("doresetTodayMission", 3)) {
		toDayMission = missionMapper.selectMissionByDate(System.currentTimeMillis());
		if (null == toDayMission || toDayMission.isEmpty()) {
			Random random = new Random();
			Mission mis = new Mission();
			mis.setMission(missionTmp.get(random.nextInt(5)));
			mis.setMstatus(0);
			mis.setMtype(DAILY);
			mis.setCreator("system");
			mis.setRank(1);
			Long dayfirts = TimeUtils4book.getTimesmorning();
			mis.setStartdate(dayfirts);
			mis.setEnddate(dayfirts + (24 * 3600000) - 1);
			saveMission(mis);
			toDayMission = missionMapper.selectMissionByDate(System.currentTimeMillis());
		}
		AllMission = missionMapper.selectAll();
		}
	}

	private List<Mission> jsonClone(List<Mission> jso) {
		return JSON.parseArray(JSON.toJSONString(jso), Mission.class);
	}

	public int saveMission(Mission mis) {
		Long nowdt = System.currentTimeMillis();
		mis.setCreatetime(nowdt);
		mis.setUpdatetime(nowdt);
		mis.setMstatus(0);
		int x = missionMapper.insert(mis);
		if (x > 0) {
			AllMission.add(mis);
		}
		return x;
	}

	public List<UserMission> getUserMission(Long userid) {
		return umMapper.selectByUserId(userid);
	}

	public List<UserMission> getUserTodayMission(Long userid) {
		return umMapper.selectByUserIdAndDate(userid, System.currentTimeMillis(), TimeUtils4book.getTimesMonthBefore());
	}

	public List<Long> getAllMissionByUserId(final Long userid) {
		return umMapper.getAllMissionByUserId(userid);
	}

	private List<Mission> AllMission = Lists.newArrayList();

	public List<Mission> getAllMission() {
		if (AllMission.isEmpty()) {
			AllMission = missionMapper.selectAll();
		}
		return AllMission;
	}

	public List<Mission> getMissionListByPage(Integer page) {
		List<Mission> pagemislis = Lists.newArrayList();
		List<Mission> lm = getAllMission();
		int size = lm.size();
		int xstart = (page - 1) * CommonStr.BIGPAGESIZE;
		if (xstart > size - 1) {
			return pagemislis;
		}
		int xmax = xstart + CommonStr.BIGPAGESIZE;
		if (size <= xmax) {
			xmax = size;
		}
		for (int i = size - xstart - 1; i >= size - xmax; i--) {
			pagemislis.add(lm.get(i));
		}
		return pagemislis;
	}

	// private static Cache<String, List<Mission>> USERMISSION_OLD =
	// CacheBuilder.newBuilder()
	// .expireAfterAccess(30,
	// TimeUnit.MINUTES).softValues().initialCapacity(128).maximumSize(32768).build();

	public List<Mission> getOldeMissonByCache(final Long userid) {
		// try {
		// return USERMISSION_OLD.get(String.valueOf(userid), new
		// Callable<List<Mission>>(){
		// @Override
		// public List<Mission> call() throws Exception {
		// return getOldMission(userid);
		// }
		// });
		// } catch (ExecutionException e) {
		// e.printStackTrace();
		// return new ArrayList<Mission>();
		// }
		return getOldMission(userid);
	}

	public List<Mission> getOldMission(Long userid) {
		List<Mission> all = getAllMission();
		Map<Long, Mission> mapMis = Maps.newHashMap();
		for (Mission mis : all) {
			mapMis.put(mis.getId(), mis);
		}
		List<Long> lilong = getAllMissionByUserId(userid);
		for (Long id : lilong) {
			mapMis.remove(id);
		}
		List<Mission> tlm = dogetTodayMission();
		for (Mission mis : tlm) {
			mapMis.remove(mis.getId());
		}
		List<Mission> rl = new ArrayList<Mission>();
		if (!mapMis.isEmpty()) {
			int o = 0;
			Set<Long> ms = mapMis.keySet();
			for (Long x : ms) {
				if (o < 2) {
					rl.add(mapMis.get(x));
				}
				o++;
			}
		}
		return rl;
	}

	public static void main(String args[]) {
		System.out.println(TimeUtils4book.getTimesmorning());
		System.out.println(System.currentTimeMillis());
		System.out.println(TimeUtils4book.getTimesmorning() + 24 * 3600 * 1000);
		// for(int i = 0;i<20;i++){
		// Random random = new Random();
		// System.out.println(random.nextInt(5));
		// }
		System.out.println(new Date(TimeUtils4book.getTimesmorning()));
		System.out.println(new Date(TimeUtils4book.getTimesmorning() + 24 * 3600 * 1000));
		System.out.println(new Date().getTime());
	}

	public int doMission(Long mid, Long bid, Long uid) {
		UserMission um = new UserMission();
		um.setBkid(bid);
		um.setUid(uid);
		um.setMid(mid);
		um.setUmstatus(0);
		um.setCreatetime(System.currentTimeMillis());
		um.setUpdatetime(System.currentTimeMillis());
		int i = umMapper.insert(um);
		if (i > 0) {
			longredisService.add2ListLeft(BookService.MISSIONBOOK + mid, bid);
		}
		return i;
	}

	public Pager<Mission> queryMissionPage(Integer ctpg, String qstr, String stime, String etime) {
		Pager<Mission> pg = new Pager<Mission>();
		Long starttime = StringUtils.isEmpty(stime) ? null
				: TimeUtils4book.str2date(stime, TimeUtils4book.yMd_).getTime();
		Long endtime = StringUtils.isEmpty(etime) ? null
				: TimeUtils4book.str2date(etime, TimeUtils4book.yMd_).getTime() + 24 * 360000L;
		pg.setCurrentPage(ctpg);
		pg.setTotalSize(missionMapper.selectTotal(qstr, starttime, endtime));
		pg.setReList(missionMapper.selectForPage(qstr, starttime, endtime, (ctpg - 1) * CommonStr.PAGESIZE,
				CommonStr.PAGESIZE));
		return pg;
	}

	public Mission getAllMissionById(Long id) {
		return missionMapper.selectByPrimaryKey(id);
	}

}
