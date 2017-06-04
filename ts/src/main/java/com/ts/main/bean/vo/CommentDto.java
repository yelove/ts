/**
 * 
 */
package com.ts.main.bean.vo;

import com.ts.main.bean.model.Comment;

/**
 * @author hasee
 *
 */
public class CommentDto extends Comment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6223025381050775487L;

	private String username;

	private String userimg;
	
	private String createTimeStr;

	private Long zan;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserimg() {
		return userimg;
	}

	public void setUserimg(String userimg) {
		this.userimg = userimg;
	}

	public Long getZan() {
		return zan;
	}

	public void setZan(Long zan) {
		this.zan = zan;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

}
