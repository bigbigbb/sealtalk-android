package com.caishi.zhanghai.im.bean;

/**
 * Created by yusy on 2018/1/3.
 */

public class UpLoadPictureReturnBean {

    /**
     * rid : xxyy
     * m : member
     * k : portrait
     * v : ok
     * desc : 设置成功
     * data : {"portraitUri":"http://xxx.xxx.jpg"}
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
         * portraitUri : http://xxx.xxx.jpg
         */

        private String portraitUri;

        public String getPortraitUri() {
            return portraitUri;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }
    }
}
