package com.caishi.zhanghai.im.bean;

import java.util.List;

/**
 * Created by yusy on 2018/1/6.
 */

public class BlackReturnBean {

    /**
     * rid : xxyy
     * m : friend
     * k : black_list
     * v : ok
     * desc : 获取成功
     * data : [{"id":"sdf9sd0df98","nickname":"Tom","portraitUri":"http://test.com/user/abc123.jpg","updatedAt":"2017-12-30T16:22:33.000Z"},{"id":"fgh809fg098","nickname":"Jerry","portraitUri":"http://test.com/user/abc234.jpg","updatedAt":"2017-12-30T16:22:33.000Z"}]
     */

    private String rid;
    private String m;
    private String k;
    private String v;
    private String desc;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : sdf9sd0df98
         * nickname : Tom
         * portraitUri : http://test.com/user/abc123.jpg
         * updatedAt : 2017-12-30T16:22:33.000Z
         */

        private String id;
        private String nickname;
        private String portraitUri;
        private String updatedAt;

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

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
