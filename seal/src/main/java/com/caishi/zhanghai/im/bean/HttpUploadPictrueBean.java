package com.caishi.zhanghai.im.bean;

/**
 * Created by yusy on 2018/1/13.
 */

public class HttpUploadPictrueBean {
    private String code;
    private String msg;
    private UploadData data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UploadData getData() {
        return data;
    }

    public void setData(UploadData data) {
        this.data = data;
    }

    public class UploadData{
        private String portraitUri;

        public String getPortraitUri() {
            return portraitUri;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }
    }
}
