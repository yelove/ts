package com.ts.main.bean;

import java.util.List;

import com.ts.main.common.CommonStr;

public class Pager<T> {

	private int pageSize = CommonStr.PAGESIZE;

	private int totalSize;

	private int totalRows;

	private int currentPage = 1;

	private List<T> reList;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getTotalRows() {
		if (totalSize > 0) {
			return (totalSize / pageSize) + 1;
		}
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public List<T> getReList() {
		return reList;
	}

	public void setReList(List<T> reList) {
		this.reList = reList;
	}

}
