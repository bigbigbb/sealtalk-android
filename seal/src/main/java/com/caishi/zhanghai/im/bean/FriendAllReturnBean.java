package com.caishi.zhanghai.im.bean;

import java.util.List;

/**
 * Created by yusy on 2017/12/30.
 */

public class FriendAllReturnBean {


    /**
     * rid : 1514622156858
     * m : friend
     * k : all
     * v : ok
     * desc : 获取成功
     * data : [{"displayName":null,"message":"我是Kkk","status":11,"updatedAt":"2017-12-30 16:22:33","user":{"id":"6641f5db22f1f0bafd2fa44527a239cf","nickname":"Kkk","region":"86","phone":"18621275009","portraitUri":"http://www.looklaw.cn/dist/img/default_head.png"}}]
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
         * displayName : null
         * message : 我是Kkk
         * status : 11
         * updatedAt : 2017-12-30 16:22:33
         * user : {"id":"6641f5db22f1f0bafd2fa44527a239cf","nickname":"Kkk","region":"86","phone":"18621275009","portraitUri":"http://www.looklaw.cn/dist/img/default_head.png"}
         */

        private String displayName;
        private String message;
        private int status;
        private String updatedAt;
        private UserBean user;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean {
            /**
             * id : 6641f5db22f1f0bafd2fa44527a239cf
             * nickname : Kkk
             * region : 86
             * phone : 18621275009
             * portraitUri : http://www.looklaw.cn/dist/img/default_head.png
             */

            private String id;
            private String nickname;
            private String region;
            private String phone;
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

            public String getRegion() {
                return region;
            }

            public void setRegion(String region) {
                this.region = region;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getPortraitUri() {
                return portraitUri;
            }

            public void setPortraitUri(String portraitUri) {
                this.portraitUri = portraitUri;
            }
        }
    }
}
