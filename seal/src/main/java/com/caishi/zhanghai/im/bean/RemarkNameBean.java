package com.caishi.zhanghai.im.bean;

/**
 * Created by shihui on 2018/1/4.
 */

public class RemarkNameBean {

    /**
     * rid : xxyy
     * m : friend
     * k : set_remark
     * v : {"friendId":"bdc59baa47627c4b38c43fe2fbc4f4ae","displayName":"备注名"}
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
         * friendId : bdc59baa47627c4b38c43fe2fbc4f4ae
         * displayName : 备注名
         */

        private String friendId;
        private String displayName;

        public String getFriendId() {
            return friendId;
        }

        public void setFriendId(String friendId) {
            this.friendId = friendId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }
}
