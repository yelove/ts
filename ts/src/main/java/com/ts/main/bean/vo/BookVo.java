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
	
	private String markdate;
	
	private String creatdate;
	
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
	
}
