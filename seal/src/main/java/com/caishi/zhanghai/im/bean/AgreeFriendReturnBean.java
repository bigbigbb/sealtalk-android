package com.caishi.zhanghai.im.bean;

/**
 * Created by yusy on 2017/12/30.
 */

public class AgreeFriendReturnBean {

    /**
     * rid : xxyy
     * m : friend
     * k : agree
     * v : ok
     * desc : 调用成功
     * data : {"action":"Added","note":"添加成功"}
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
         * action : Added
         * note : 添加成功
         */

        private String action;
        private String note;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
}
