package com.ts.main.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.Statement;
import com.ts.main.bean.Msg;

@Service
public class MsgDao {

	@Autowired
	private JdbcTemplate template;

	public long saveMsg(final Msg msg) {
		msg.setStatus(0);
		msg.setCreateTime(System.currentTimeMillis());
		msg.setUpdateTime(System.currentTimeMillis());
		KeyHolder kh = new GeneratedKeyHolder();
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(
						"INSERT into `msg` (no,msg,msgDesc,status,createTime,updatetime) VALUES (?,?,?,?,?,?);",Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, msg.getNo());
				ps.setString(2, msg.getMsg());
				ps.setString(3, msg.getMsgDesc());
				ps.setInt(4, msg.getStatus());
				ps.setLong(5, msg.getCreateTime());
				ps.setLong(6, msg.getUpdateTime());
				return ps;
			}
		}, kh);
		return kh.getKey().longValue();
	}

	public Msg getMsg(Long id) {
		return template.queryForObject("select * from `msg` where id = ?", Msg.class, id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Msg getMsg(String msg) {
		return (Msg) template.queryForObject("select * from `msg` where msg like ?", new Object[] { "%" + msg + "%" },
				new BeanPropertyRowMapper(Msg.class));
	}

	public boolean updateUserForLastLog(Long id, Long updateTime) {
		int i = template.update("UPDATE `msg` set updateTime=?,status=2 where id=?", updateTime, id);
		if (i > 0) {
			return true;
		}
		return false;
	}

	public boolean updateMsg(Long id) {
		int i = template.update("UPDATE `msg` set updateTime=?,status=2 where id=?", System.currentTimeMillis(), id);
		if (i > 0) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public int count(String qstr) {
		String sql = "select count(1) from `msg` where status=2 and  msg like ?";
		return template.queryForInt(sql, "%" + qstr + "%");
	}

	public List<Msg> getOldMsg(int currentPage, int pagesize, String qstr) {
		String sql = "select * from `msg` where status=2 and msg like ? limit ?,?";
		List<Map<String, Object>> rsm = template.queryForList(sql,
				new Object[] { "%" + qstr + "%", pagesize * (currentPage - 1), pagesize });
		List<Msg> rsod = new ArrayList<Msg>();
		for (@SuppressWarnings("rawtypes")
		Map rs : rsm) {
			Msg od = new Msg();
			try {
				BeanUtils.populate(od, rs);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rsod.add(od);
		}
		return rsod;
	}

	public List<Msg> getNewMsg() {
		String sql = "select * from `msg` where status=0 ";
		List<Map<String, Object>> rsm = template.queryForList(sql);
		List<Msg> rsod = new ArrayList<Msg>();
		for (@SuppressWarnings("rawtypes")
		Map rs : rsm) {
			Msg od = new Msg();
			try {
				BeanUtils.populate(od, rs);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rsod.add(od);
		}
		return rsod;
	}
	
	@SuppressWarnings("deprecation")
	public int countTodayMsg(Long time){
		String sql = "select count(1) from `msg` where createTime > ?";
		return template.queryForInt(sql, time);
	}

}
