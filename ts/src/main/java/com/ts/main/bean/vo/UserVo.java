package com.ts.main.bean.vo;

import com.ts.main.bean.model.User;

public class UserVo extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2179777261526715576L;
	
	private String byear;
	
	private String bmonth;
	
	private String bday;

	public String getByear() {
		return byear;
	}

	public void setByear(String byear) {
		this.byear = byear;
	}

	public String getBmonth() {
		return bmonth;
	}

	public void setBmonth(String bmonth) {
		this.bmonth = bmonth;
	}

	public String getBday() {
		return bday;
	}

	public void setBday(String bday) {
		this.bday = bday;
	}
	

}
