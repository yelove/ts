/**
 * 
 */
package com.ts.main.bean.vo;

import java.io.Serializable;

/**
 * @author hasee
 *
 */
public class ID123 implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1956586932904331015L;

	private Long bkid;

	private Long[] id23;
	
	

	public ID123() {
	}

	public ID123(Long bookId1, Long bookId2, Long bookId3) {
		bkid = bookId1;
		id23 = new Long[] { bookId2, bookId3 };
	}

	public Long getBkid() {
		return bkid;
	}

	public void setBkid(Long bkid) {
		this.bkid = bkid;
	}

	public Long[] getId23() {
		return id23;
	}

	public void setId23(Long[] id23) {
		this.id23 = id23;
	}
}
