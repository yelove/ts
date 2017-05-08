/**
 * 
 */
package com.ts.main.bean;

import java.io.Serializable;

/**
 * @author hasee
 *
 */
public class Book implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8872191478076949856L;
	
	private Long id;
	
	private Long createtime;
	
	private Long updatetime;
	
	private int isopen;
	
	private String text;
	
	private Long userid;
	
	private Long marktime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

	public Long getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Long updatetime) {
		this.updatetime = updatetime;
	}

	public int getIsopen() {
		return isopen;
	}

	public void setIsopen(int isopen) {
		this.isopen = isopen;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getMarktime() {
		return marktime;
	}

	public void setMarktime(Long marktime) {
		this.marktime = marktime;
	}
	
}
