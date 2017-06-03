/**
 * 
 */
package com.ts.main.bean.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author hasee
 *
 */
public class CommentVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1304343347214045068L;
	
	private List<CommentDto> hotList;
	
	private List<CommentDto> nomalList;
	
	private Integer totalSize;

	public List<CommentDto> getHotList() {
		return hotList;
	}

	public void setHotList(List<CommentDto> hotList) {
		this.hotList = hotList;
	}

	public List<CommentDto> getNomalList() {
		return nomalList;
	}

	public void setNomalList(List<CommentDto> nomalList) {
		this.nomalList = nomalList;
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}
	
}
