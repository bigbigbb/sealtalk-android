package com.caishi.zhanghai.im.bean;

/**
 * Created by shihui on 2018/1/5.
 */

public class UpdatePwdBean {

    /**
     * rid : xxyy
     * m : member
     * k : pwd_via_pwd
     * v : {"oldPassword":"e10adc3949ba59abbe56e057f20f883e","newPassword":"6309d2d98922359173b9931e9110ed2c"}
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
         * oldPassword : e10adc3949ba59abbe56e057f20f883e
         * newPassword : 6309d2d98922359173b9931e9110ed2c
         */

        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
