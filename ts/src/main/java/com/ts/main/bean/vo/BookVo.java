/**
 * 
 */
package com.ts.main.bean.vo;

import java.util.List;

import com.ts.main.bean.model.Book;

/**
 * @author hasee
 *
 */
public class BookVo extends Book{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6185161233549210168L;
	
	private Long tsno;
	
	private String markdate;
	
	private String userImgUrl;
	
	private String userName;
	
	private String creatdate;
	
	private String interval;
	
	private String updatedate;
	
	private List<BookVo> nearlist;

	public String getMarkdate() {
		return markdate;
	}

	public void setMarkdate(String markdate) {
		this.markdate = markdate;
	}

	public String getCreatdate() {
		return creatdate;
	}

	public void setCreatdate(String creatdate) {
		this.creatdate = creatdate;
	}

	public String getUpdatedate() {
		return updatedate;
	}

	public void setUpdatedate(String updatedate) {
		this.updatedate = updatedate;
	}

	public List<BookVo> getNearlist() {
		return nearlist;
	}

	public void setNearlist(List<BookVo> nearlist) {
		this.nearlist = nearlist;
	}

	public Long getTsno() {
		return tsno;
	}

	public void setTsno(Long tsno) {
		this.tsno = tsno;
	}

	public String getUserImgUrl() {
		return userImgUrl;
	}

	public void setUserImgUrl(String userImgUrl) {
		this.userImgUrl = userImgUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}
}
