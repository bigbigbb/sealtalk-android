package com.caishi.zhanghai.im.bean;

/**
 * Created by yusy on 2017/12/17.
 */

public class GetCodeReturnBean {


    /**
     * rid : xxx
     * m : common
     * k : get_smscode_reg
     * v : ok
     * desc : 验证码发送成功
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
