package com.caishi.zhanghai.im.bean;

/**
 * Created by shihui on 2017/12/29.
 */

public class SearchFriendReturnBean {

    /**
     * rid : xxyy
     * m : common
     * k : m2acc
     * v : ok
     * desc : 获取成功
     * data : {"id":"c73480767ffd0840652d3da4f44f0466","nickname":"付小涛","portraitUri":"http://xxx.com/xx.png"}
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
         * id : c73480767ffd0840652d3da4f44f0466
         * nickname : 付小涛
         * portraitUri : http://xxx.com/xx.png
         */

        private String id;
        private String nickname;
        private String portraitUri;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPortraitUri() {
            return portraitUri;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }
    }
}
