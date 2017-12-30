package com.caishi.zhanghai.im;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;


import com.caishi.zhanghai.im.bean.GetUserInfoBean;
import com.caishi.zhanghai.im.bean.GetUserInfoReturnBean;
import com.caishi.zhanghai.im.net.CallBackJson;
import com.caishi.zhanghai.im.net.SocketClient;
import com.caishi.zhanghai.im.server.SealAction;
import com.caishi.zhanghai.im.server.network.async.AsyncTaskManager;
import com.caishi.zhanghai.im.server.network.async.OnDataListener;
import com.caishi.zhanghai.im.server.network.http.HttpException;
import com.caishi.zhanghai.im.server.response.GetUserInfoByIdResponse;
import com.google.gson.Gson;

import io.rong.imlib.model.UserInfo;

/**
 * 用户信息提供者的异步请求类
 * Created by AMing on 15/12/10.
 * Company RongCloud
 */
public class UserInfoEngine implements OnDataListener {


    private static UserInfoEngine instance;
    private UserInfoListener mListener;
    private Context context;

    public static UserInfoEngine getInstance(Context context) {
        if (instance == null) {
            instance = new UserInfoEngine(context);
        }
        return instance;
    }

    private UserInfoEngine(Context context) {
        this.context = context;
    }


    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private static final int REQUSERINFO = 4234;

    public void startEngine(String userid) {
        setUserid(userid);
//        AsyncTaskManager.getInstance(context).request(userid, REQUSERINFO, this);
        syncUserInfo();
    }


    private void syncUserInfo(){
        GetUserInfoBean getUserInfoBean = new GetUserInfoBean();
        getUserInfoBean.setK("user_info");
        getUserInfoBean.setM("member");
        getUserInfoBean.setRid(String.valueOf(System.currentTimeMillis()));
        GetUserInfoBean.VBean  vBean = new GetUserInfoBean.VBean();
        vBean.setId(userid);
        getUserInfoBean.setV(vBean);
        String  msg = new Gson().toJson(getUserInfoBean);
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                GetUserInfoReturnBean getUserInfoReturnBean = new Gson().fromJson(json,GetUserInfoReturnBean.class);
                Message message = new Message();
                message.obj = getUserInfoReturnBean;
                message.what = 1;
                handler.sendMessage(message);
            }
        });

    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    GetUserInfoReturnBean getUserInfoReturnBean = (GetUserInfoReturnBean)msg.obj;
                    if(null!=getUserInfoReturnBean.getData()){
                        GetUserInfoReturnBean.DataBean dataBean = getUserInfoReturnBean.getData();
                        UserInfo userInfo = new UserInfo(dataBean.getId(), dataBean.getNickname(), Uri.parse(dataBean.getPortraitUri()));
                        if (mListener != null) {
                            mListener.onResult(userInfo);
                        }
                    }

                    break;
            }
        }
    };



    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        return new SealAction(context).getUserInfoById(id);
    }


    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            GetUserInfoByIdResponse res = (GetUserInfoByIdResponse) result;
            if (res.getCode() == 200) {
                UserInfo userInfo = new UserInfo(res.getResult().getId(), res.getResult().getNickname(), Uri.parse(res.getResult().getPortraitUri()));
                if (mListener != null) {
                    mListener.onResult(userInfo);
                }
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        if (mListener != null) {
            mListener.onResult(null);
        }
    }

    public void setListener(UserInfoListener listener) {
        this.mListener = listener;
    }

    public interface UserInfoListener {
        void onResult(UserInfo info);
    }
}
