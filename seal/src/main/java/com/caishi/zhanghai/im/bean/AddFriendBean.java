package com.caishi.zhanghai.im.bean;

/**
 * Created by shihui on 2017/12/29.
 */

public class AddFriendBean {


    /**
     * rid : xxyy
     * m : friend
     * k : invite
     * v : {"friendId":"c73480767ffd0840652d3da4f44f0466","message":"您好，我是付小涛"}
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
         * friendId : c73480767ffd0840652d3da4f44f0466
         * message : 您好，我是付小涛
         */

        private String friendId;
        private String message;

        public String getFriendId() {
            return friendId;
        }

        public void setFriendId(String friendId) {
            this.friendId = friendId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
