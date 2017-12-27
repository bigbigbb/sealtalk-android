package com.caishi.zhanghai.im.bean;

/**
 * Created by shihui on 2017/12/15.
 */

public class GetUrlBean {

    /**
     * error_code : 0
     * data : {"ip":"139.224.115.197","port":"8888"}
     */

    private int error_code;
    private DataBean data;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * ip : 139.224.115.197
         * port : 8888
         */

        private String ip;
        private String port;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }
}
