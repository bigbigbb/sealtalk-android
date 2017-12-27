package com.caishi.zhanghai.im.bean;

/**
 * Created by yusy on 2017/12/17.
 */

public class LoginBean {

    /**
     * rid : xxyy
     * m : member
     * k : login_pass
     * v : {"mobile":"13127655805","password":"cd369124e55ffdc3edd51f04c330d8f5"}
     */

    private String rid;
    private String m;
    private String k;
    private VBean v;

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

    public VBean getV() {
        return v;
    }

    public void setV(VBean v) {
        this.v = v;
    }

    public static class VBean {
        /**
         * mobile : 13127655805
         * password : cd369124e55ffdc3edd51f04c330d8f5
         */

        private String mobile;
        private String password;

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
