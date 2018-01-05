package com.caishi.zhanghai.im.bean;

/**
 * Created by shihui on 2018/1/5.
 */

public class BaseReturnBean {

    /**
     * rid : xxyy
     * m : member
     * k : pwd_via_pwd
     * v : ok
     * desc : 密码修改成功
     */

    private String rid;
    private String m;
    private String k;
    private String v;
    private String desc;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
