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
	
	/**
	 * 是否公开
	 */
	private int isopen;
	
	/**
	 * 文章内容
	 */
	private String text;
	
	private Long userid;
	
	/**
	 * 时间点
	 */
	private Long marktime;
	
	/**
	 * 阅读量
	 */
	private Long readnum;
	
	/**
	 * 点赞数量
	 */
	private Long praisenum;
	
	/**
	 * 吐槽数量
	 */
	private Long spitnum;
	
	/**
	 * 评论数量
	 */
	private Long commentnum;

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

	public Long getReadnum() {
		return readnum;
	}

	public void setReadnum(Long readnum) {
		this.readnum = readnum;
	}

	public Long getPraisenum() {
		return praisenum;
	}

	public void setPraisenum(Long praisenum) {
		this.praisenum = praisenum;
	}

	public Long getSpitnum() {
		return spitnum;
	}

	public void setSpitnum(Long spitnum) {
		this.spitnum = spitnum;
	}

	public Long getCommentnum() {
		return commentnum;
	}

	public void setCommentnum(Long commentnum) {
		this.commentnum = commentnum;
	}
	
}
