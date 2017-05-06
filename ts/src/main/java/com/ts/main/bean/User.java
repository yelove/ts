package com.ts.main.bean;

import java.io.Serializable;
import java.util.Date;

import com.ts.main.util.TimeUtils;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8931334137732090447L;

	private long id;
	
	private long tsno;
	
	private String email;

	private String name;
	
	private String namemd5;

	private String password;

	private long departmentid;

	private String department;

	private String realname;

	private int state;

	private long createtime;

	private long updatetime;
	
	private String upstr;
	
	
	public long getTsno() {
		return tsno;
	}

	public void setTsno(long tsno) {
		this.tsno = tsno;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamemd5() {
		return namemd5;
	}

	public void setNamemd5(String namemd5) {
		this.namemd5 = namemd5;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(long departmentid) {
		this.departmentid = departmentid;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}


	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public long getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(long updatetime) {
		this.updatetime = updatetime;
	}
	
	public String getUpstr() {
		if(updatetime>0){
			upstr = TimeUtils.date2str(new Date(updatetime));
		}
		return upstr;
	}

	public void setUpstr(String upstr) {
		this.upstr = upstr;
	}

}
