package com.caishi.zhanghai.im.bean;

/**
 * Created by shihui on 2017/12/19.
 */

public class LoginReturnBean {

    /**
     * rid : 1513649695344
     * m : member
     * k : login_pass
     * v : ok
     * desc : 登录成功
     * data : {"account":"aeed0d2463dfe72f671a6e49006160e0","token":"201df4cb299e05e6d3734e0cbafa098c"}
     */

    private String rid;
    private String m;
    private String k;
    private String v;
    private String desc;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * account : aeed0d2463dfe72f671a6e49006160e0
         * token : 201df4cb299e05e6d3734e0cbafa098c
         */

        private String account;
        private String token;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
