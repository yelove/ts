/**
 * 
 */
package com.ts.main.bean.vo;

/**
 * @author ZSY
 *
 */
public class Page {
	
	public static final int LIMITNUM = 30;
	
	private Integer page = 0;
	
	private Integer limit = LIMITNUM;
	
	private Integer totalPage;
	
	private Long totalRows;
	
	private Integer term = 3;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Long getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(Long totalRows) {
		this.totalRows = totalRows;
	}

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}
	
}
