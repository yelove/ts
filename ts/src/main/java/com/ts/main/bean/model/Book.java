package com.ts.main.bean.model;

import java.io.Serializable;

public class Book implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5732005956967970176L;

	private Long id;

    private Long createtime;

    private Long updatetime;

    private Integer isopen;

    private String text;

    private Long userid;

    private Integer isdel;

    private Long marktime;

    private Long readnum;

    private Long praisenum;

    private Long spitnum;

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

    public Integer getIsopen() {
        return isopen;
    }

    public void setIsopen(Integer isopen) {
        this.isopen = isopen;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? null : text.trim();
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Integer getIsdel() {
        return isdel;
    }

    public void setIsdel(Integer isdel) {
        this.isdel = isdel;
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