package com.caishi.zhanghai.im.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.caishi.zhanghai.im.R;
import com.caishi.zhanghai.im.SealAppContext;
import com.caishi.zhanghai.im.SealConst;
import com.caishi.zhanghai.im.SealUserInfoManager;
import com.caishi.zhanghai.im.bean.GetUserInfoReturnBean;
import com.caishi.zhanghai.im.bean.LoginBean;
import com.caishi.zhanghai.im.bean.LoginReturnBean;
import com.caishi.zhanghai.im.net.AppParm;
import com.caishi.zhanghai.im.net.CallBackJson;
import com.caishi.zhanghai.im.net.GetUrlUtil;
import com.caishi.zhanghai.im.net.SocketClient;
import com.caishi.zhanghai.im.server.broadcast.BroadcastManager;
import com.caishi.zhanghai.im.server.utils.NLog;
import com.caishi.zhanghai.im.utils.MD5;
import com.google.gson.Gson;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/8/5.
 * Company RongCloud
 */
public class SplashActivity extends Activity {

    private Context context;
    private android.os.Handler handler = new android.os.Handler();
    private SocketClient mSocketClient;
    private String name,pwd;
    private boolean isFrom = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        context = this;
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String cacheToken = sp.getString("loginToken", "");
          name = sp.getString(SealConst.SEALTALK_LOGING_PHONE,"");
          pwd = sp.getString(SealConst.SEALTALK_LOGING_PASSWORD,"");


          isFrom = true;
        BroadcastManager.getInstance(getApplication()).addAction(SealConst.BREAK_UP, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isFrom = false;
                initSocketNet();
            }
        });
        initSocketNet();
//        if (!TextUtils.isEmpty(cacheToken)) {
//            RongIM.connect(cacheToken, SealAppContext.getInstance().getConnectCallback());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    goToMain();
//                }
//            }, 1000);
//        } else {
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    goToLogin();
//                }
//            }, 800);
//        }






    }

    public void  initSocketNet(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetUrlUtil.requestGet();
                Looper.prepare();
                if(null!= AppParm.IP&&null!=AppParm.PORT){
                    mSocketClient = SocketClient.getInstance();
                    mSocketClient.initSocket(getApplication());
                    if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(pwd)) {
                        login(name,pwd);
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(isFrom){
                                    goToLogin();
                                }

                            }
                        }, 800);
                    }
                }
                Looper.loop();
            }
        }).start();


    }
    private void login(final String mobile, final String pwd) {

        LoginBean loginBean = new LoginBean();
        LoginBean.VBean vBean = new LoginBean.VBean();
        vBean.setPassword(MD5.getStringMD5(pwd));
        vBean.setMobile(mobile);
        loginBean.setV(vBean);
        loginBean.setM("member");
        loginBean.setK("login_pass");
        loginBean.setRid(String.valueOf(System.currentTimeMillis()));

        String msg = new Gson().toJson(loginBean);
        Log.e("msg000",msg);
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                Log.e("test000", "json" + json);
                LoginReturnBean loginReturnBean = new Gson().fromJson(json, LoginReturnBean.class);
                Message message = new Message();
                message.obj = loginReturnBean;
                message.what =0;
                handler1.sendMessage(message);
            }
        });

    }

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    LoginReturnBean loginReturnBean = (LoginReturnBean) msg.obj;
                    Toast.makeText(getApplication(), loginReturnBean.getDesc(), Toast.LENGTH_LONG).show();
                    if (loginReturnBean.getV().equals("ok")) {
                        if (null != loginReturnBean.getData()) {
                            RongIM.connect(loginReturnBean.getData().getToken(), SealAppContext.getInstance().getConnectCallback());
                            if(isFrom){
                                goToMain();
                            }

                        }
                    }
                    break;

            }


        }
    };

    private void goToMain() {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

}
