/**
 * Copyright©2016 zsy. all rights reserved.
 *
 * @Title Msg.java
 * @Prject bidding
 * @Package com.taika.bidding.bean
 * @author 张绍云
 * @date 2016年6月17日 上午10:34:20
 * @Description  
 */
package com.ts.main.bean;

import java.io.Serializable;
import java.util.Date;

import com.ts.main.util.TimeUtils;

/**
 * @ClassName Msg
 * @Description
 * @author 张绍云
 * @date 2016年6月17日 上午10:34:20
 */
public class Msg implements Serializable {

	/**
	 * @fieldName serialVersionUID
	 * @fieldType long
	 * @Description
	 */
	private static final long serialVersionUID = 6476768815609437811L;

	private long id;

	private String no;

	private String msg;

	private String msgDesc;

	private String ctstr = "";

	private Long createTime;

	private Long updateTime;

	private int status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsgDesc() {
		return msgDesc;
	}

	public void setMsgDesc(String msgDesc) {
		this.msgDesc = msgDesc;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCtstr() {
		if (createTime > 0) {
			ctstr = TimeUtils.date2str(new Date(createTime));
		}
		return ctstr;
	}

	public void setCtstr(String ctstr) {
		this.ctstr = ctstr;
	}

}
