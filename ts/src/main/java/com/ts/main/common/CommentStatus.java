/**
 * 
 */
package com.ts.main.common;

/**
 * @author hasee
 *
 */
public enum CommentStatus {
	
	/**
	 * NOMAL 正常 HOT 热评 CREAM 精华 DEL 自己删除 BEDEL 编辑删除
	 */
	NOMAL(0), HOT(1), CREAM(2), DEL(-1), BEDEL(-2);

	private int value;

	private CommentStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
