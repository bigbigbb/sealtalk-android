package com.caishi.zhanghai.im.bean;

/**
 * Created by yusy on 2017/12/17.
 */

public class SetPassBean {


    /**
     * rid : xxx
     * m : member
     * k : reg_mobile
     * v : {"name":"付小涛","mobile":"13127655805","sms_code":"1234","password":"123456"}
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
         * name : 付小涛
         * mobile : 13127655805
         * sms_code : 1234
         * password : 123456
         */

        private String name;
        private String mobile;
        private String sms_code;
        private String password;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getSms_code() {
            return sms_code;
        }

        public void setSms_code(String sms_code) {
            this.sms_code = sms_code;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
