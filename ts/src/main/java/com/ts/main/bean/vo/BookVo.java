/**
 * 
 */
package com.ts.main.bean.vo;

import com.ts.main.bean.Book;

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
	
}
