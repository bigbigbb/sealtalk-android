package com.caishi.zhanghai.im.bean;

/**
 * Created by shihui on 2017/12/15.
 */

public class HeartBean {

    /**
     * rid : xxx
     * m : system
     * k : pong
     * v : 客户端时间戳
     */

    private String rid;
    private String m;
    private String k;
    private String v;

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
}
