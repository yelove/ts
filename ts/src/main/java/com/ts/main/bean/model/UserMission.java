package com.ts.main.bean.model;

public class UserMission extends UserMissionKey {
    private Long createtime;

    private Long updatetime;

    private Integer umstatus;

    private Long bkid;

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

    public Integer getUmstatus() {
        return umstatus;
    }

    public void setUmstatus(Integer umstatus) {
        this.umstatus = umstatus;
    }

    public Long getBkid() {
        return bkid;
    }

    public void setBkid(Long bkid) {
        this.bkid = bkid;
    }
}