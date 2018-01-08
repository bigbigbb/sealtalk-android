package com.caishi.zhanghai.im.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.caishi.zhanghai.im.R;
import com.caishi.zhanghai.im.SealConst;
import com.caishi.zhanghai.im.SealUserInfoManager;
import com.caishi.zhanghai.im.bean.FriendAllBean;
import com.caishi.zhanghai.im.bean.FriendAllReturnBean;
import com.caishi.zhanghai.im.bean.GetUserInfoBean;
import com.caishi.zhanghai.im.bean.GetUserInfoReturnBean;
import com.caishi.zhanghai.im.bean.LoginBean;
import com.caishi.zhanghai.im.bean.LoginReturnBean;
import com.caishi.zhanghai.im.net.CallBackJson;
import com.caishi.zhanghai.im.net.SocketClient;
import com.caishi.zhanghai.im.server.network.http.HttpException;
import com.caishi.zhanghai.im.server.response.GetTokenResponse;
import com.caishi.zhanghai.im.server.response.GetUserInfoByIdResponse;
import com.caishi.zhanghai.im.server.response.LoginResponse;
import com.caishi.zhanghai.im.server.utils.AMUtils;
import com.caishi.zhanghai.im.server.utils.CommonUtils;
import com.caishi.zhanghai.im.server.utils.NLog;
import com.caishi.zhanghai.im.server.utils.NToast;
import com.caishi.zhanghai.im.server.utils.RongGenerate;
import com.caishi.zhanghai.im.server.widget.ClearWriteEditText;
import com.caishi.zhanghai.im.server.widget.LoadDialog;
import com.caishi.zhanghai.im.utils.MD5;
import com.google.gson.Gson;
import com.huawei.hms.support.api.entity.hwid.GetLoginInfoResult;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/1/15.
 * Company RongCloud
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "LoginActivity";
    private static final int LOGIN = 5;
    private static final int GET_TOKEN = 6;
    private static final int SYNC_USER_INFO = 9;

    private ImageView mImg_Background;
    private ClearWriteEditText mPhoneEdit, mPasswordEdit;
    private String phoneString;
    private String passwordString;
    private String connectResultId;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String loginToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setHeadVisibility(View.GONE);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        initView();
    }

    private void initView() {
        mPhoneEdit = (ClearWriteEditText) findViewById(R.id.de_login_phone);
        mPasswordEdit = (ClearWriteEditText) findViewById(R.id.de_login_password);
        Button mConfirm = (Button) findViewById(R.id.de_login_sign);
        TextView mRegister = (TextView) findViewById(R.id.de_login_register);
        TextView forgetPassword = (TextView) findViewById(R.id.de_login_forgot);
        forgetPassword.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);
        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    AMUtils.onInactive(mContext, mPhoneEdit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String oldPhone = sp.getString(SealConst.SEALTALK_LOGING_PHONE, "");
        String oldPassword = sp.getString(SealConst.SEALTALK_LOGING_PASSWORD, "");

        if (!TextUtils.isEmpty(oldPhone)) {
            mPhoneEdit.setText(oldPhone);

        }  if (!TextUtils.isEmpty(oldPassword)) {
            mPasswordEdit.setText(oldPassword);
        }


        if (getIntent().getBooleanExtra("kickedByOtherClient", false)) {
            final AlertDialog dlg = new AlertDialog.Builder(LoginActivity.this).create();
            dlg.show();
            Window window = dlg.getWindow();
            window.setContentView(R.layout.other_devices);
            TextView text = (TextView) window.findViewById(R.id.ok);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.cancel();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.de_login_sign:
                phoneString = mPhoneEdit.getText().toString().trim();
                passwordString = mPasswordEdit.getText().toString().trim();

                if (TextUtils.isEmpty(phoneString)) {
                    NToast.shortToast(mContext, R.string.phone_number_is_null);
                    mPhoneEdit.setShakeAnimation();
                    return;
                }

//                if (!AMUtils.isMobile(phoneString)) {
//                    NToast.shortToast(mContext, R.string.Illegal_phone_number);
//                    mPhoneEdit.setShakeAnimation();
//                    return;
//                }

                if (TextUtils.isEmpty(passwordString)) {
                    NToast.shortToast(mContext, R.string.password_is_null);
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                if (passwordString.contains(" ")) {
                    NToast.shortToast(mContext, R.string.password_cannot_contain_spaces);
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                LoadDialog.show(mContext);
                editor.putBoolean("exit", false);
                editor.apply();
                String oldPhone = sp.getString(SealConst.SEALTALK_LOGING_PHONE, "");
//                request(LOGIN, true);
                login(phoneString, passwordString);
                break;
            case R.id.de_login_register:
                startActivityForResult(new Intent(this, RegisterActivity.class), 1);
                break;
            case R.id.de_login_forgot:
                startActivityForResult(new Intent(this, ForgetPasswordActivity.class), 2);
                break;
        }
    }


    private void syncUserInfo() {
        GetUserInfoBean getUserInfoBean = new GetUserInfoBean();
        getUserInfoBean.setK("user_info");
        getUserInfoBean.setM("member");
        getUserInfoBean.setRid(String.valueOf(System.currentTimeMillis()));
        GetUserInfoBean.VBean vBean = new GetUserInfoBean.VBean();
        vBean.setId(connectResultId);
        getUserInfoBean.setV(vBean);
        String msg = new Gson().toJson(getUserInfoBean);
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                GetUserInfoReturnBean getUserInfoReturnBean = new Gson().fromJson(json, GetUserInfoReturnBean.class);
                Message message = new Message();
                message.obj = getUserInfoReturnBean;
                message.what = 1;
                handler.sendMessage(message);
            }
        });

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
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                Log.e("test", "json" + json);
                LoginReturnBean loginReturnBean = new Gson().fromJson(json, LoginReturnBean.class);
                Message message = new Message();
                message.obj = loginReturnBean;
                message.what = 0;
                handler.sendMessage(message);


            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    LoginReturnBean loginReturnBean = (LoginReturnBean) msg.obj;
                    Toast.makeText(getApplication(), loginReturnBean.getDesc(), Toast.LENGTH_LONG).show();
                    LoadDialog.dismiss(mContext);
                    if (loginReturnBean.getV().equals("ok")) {
                        if (null != loginReturnBean.getData()) {
                            loginToken = loginReturnBean.getData().getToken();
                            if (!TextUtils.isEmpty(loginToken)) {
                                RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
                                    @Override
                                    public void onTokenIncorrect() {
                                        NLog.e("connect", "onTokenIncorrect");
//                                    reGetToken();

                                    }

                                    @Override
                                    public void onSuccess(String s) {
                                        connectResultId = s;
                                        NLog.e("connect", "onSuccess userid:" + s);
                                        editor.putString(SealConst.SEALTALK_LOGIN_ID, s);
                                        editor.apply();
                                        SealUserInfoManager.getInstance().openDB();
                                        syncUserInfo();
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        NLog.e("connect", "onError errorcode:" + errorCode.getValue());
                                    }
                                });
                            }

                        }
                    }
                    break;
                case 1:
                    GetUserInfoReturnBean getUserInfoReturnBean = (GetUserInfoReturnBean) msg.obj;
                    GetUserInfoReturnBean.DataBean dataBean = getUserInfoReturnBean.getData();
                    if (null != dataBean) {
                        String nickName = dataBean.getNickname();
                        String portraitUri = dataBean.getPortraitUri();
                        editor.putString(SealConst.SEALTALK_LOGIN_NAME, nickName);
                        editor.putString(SealConst.SEALTALK_LOGING_PORTRAIT, portraitUri);
                        editor.apply();
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(connectResultId, nickName, Uri.parse(portraitUri)));
                    }

                    getAllFriendShip();
                    break;
                case 2:
                    FriendAllReturnBean friendAllReturnBean = (FriendAllReturnBean) msg.obj;
                    //不继续在login界面同步好友,群组,群组成员信息
                    SealUserInfoManager.getInstance().setFriendAllReturnBean(friendAllReturnBean);
                    SealUserInfoManager.getInstance().getAllUserInfo();
                    goToMain();
                    break;
            }


        }
    };

    private void getAllFriendShip() {
        FriendAllBean friendAllBean = new FriendAllBean();
        friendAllBean.setK("all");
        friendAllBean.setM("friend");
        friendAllBean.setRid(String.valueOf(System.currentTimeMillis()));
        String msg = new Gson().toJson(friendAllBean);
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                Log.e("msg1111", json);
                FriendAllReturnBean friendAllReturnBean = new Gson().fromJson(json, FriendAllReturnBean.class);
                if (null != friendAllReturnBean) {
                    Message message = new Message();
                    message.obj = friendAllReturnBean;
                    message.what = 2;
                    handler.sendMessage(message);
                }


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && data != null) {
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            mPhoneEdit.setText(phone);
            mPasswordEdit.setText(password);
        } else if (data != null && requestCode == 1) {
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            String id = data.getStringExtra("id");
            String nickname = data.getStringExtra("nickname");
//            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(id) && !TextUtils.isEmpty(nickname)) {
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(nickname)) {
                mPhoneEdit.setText(phone);
                mPasswordEdit.setText(password);
                editor.putString(SealConst.SEALTALK_LOGING_PHONE, phone);
                editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, password);
                editor.putString(SealConst.SEALTALK_LOGIN_ID, id);
                editor.putString(SealConst.SEALTALK_LOGIN_NAME, nickname);
                editor.apply();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case LOGIN:
                return action.login("86", phoneString, passwordString);
            case GET_TOKEN:
                return action.getToken();
            case SYNC_USER_INFO:
                return action.getUserInfoById(connectResultId);
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case LOGIN:
                    LoginResponse loginResponse = (LoginResponse) result;
                    if (loginResponse.getCode() == 200) {
                        loginToken = loginResponse.getResult().getToken();
                        if (!TextUtils.isEmpty(loginToken)) {
                            RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
                                @Override
                                public void onTokenIncorrect() {
                                    NLog.e("connect", "onTokenIncorrect");
                                    reGetToken();
                                }

                                @Override
                                public void onSuccess(String s) {
                                    connectResultId = s;
                                    NLog.e("connect", "onSuccess userid:" + s);
                                    editor.putString(SealConst.SEALTALK_LOGIN_ID, s);
                                    editor.apply();
                                    SealUserInfoManager.getInstance().openDB();
                                    request(SYNC_USER_INFO, true);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    NLog.e("connect", "onError errorcode:" + errorCode.getValue());
                                }
                            });
                        }
                    } else if (loginResponse.getCode() == 100) {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, R.string.phone_or_psw_error);
                    } else if (loginResponse.getCode() == 1000) {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, R.string.phone_or_psw_error);
                    }
                    break;
                case SYNC_USER_INFO:
                    GetUserInfoByIdResponse userInfoByIdResponse = (GetUserInfoByIdResponse) result;
                    if (userInfoByIdResponse.getCode() == 200) {
                        if (TextUtils.isEmpty(userInfoByIdResponse.getResult().getPortraitUri())) {
                            userInfoByIdResponse.getResult().setPortraitUri(RongGenerate.generateDefaultAvatar(userInfoByIdResponse.getResult().getNickname(), userInfoByIdResponse.getResult().getId()));
                        }
                        String nickName = userInfoByIdResponse.getResult().getNickname();
                        String portraitUri = userInfoByIdResponse.getResult().getPortraitUri();
                        editor.putString(SealConst.SEALTALK_LOGIN_NAME, nickName);
                        editor.putString(SealConst.SEALTALK_LOGING_PORTRAIT, portraitUri);
                        editor.apply();
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(connectResultId, nickName, Uri.parse(portraitUri)));
                    }
                    //不继续在login界面同步好友,群组,群组成员信息
                    SealUserInfoManager.getInstance().getAllUserInfo();
                    goToMain();
                    break;
                case GET_TOKEN:
                    GetTokenResponse tokenResponse = (GetTokenResponse) result;
                    if (tokenResponse.getCode() == 200) {
                        String token = tokenResponse.getResult().getToken();
                        if (!TextUtils.isEmpty(token)) {
                            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                                @Override
                                public void onTokenIncorrect() {
                                    Log.e(TAG, "reToken Incorrect");
                                }

                                @Override
                                public void onSuccess(String s) {
                                    connectResultId = s;
                                    NLog.e("connect", "onSuccess userid:" + s);
                                    editor.putString(SealConst.SEALTALK_LOGIN_ID, s);
                                    editor.apply();
                                    SealUserInfoManager.getInstance().openDB();
                                    request(SYNC_USER_INFO, true);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode e) {

                                }
                            });
                        }
                    }
                    break;
            }
        }
    }

    private void reGetToken() {
        request(GET_TOKEN);
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        if (!CommonUtils.isNetworkConnected(mContext)) {
            LoadDialog.dismiss(mContext);
            NToast.shortToast(mContext, getString(R.string.network_not_available));
            return;
        }
        switch (requestCode) {
            case LOGIN:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, R.string.login_api_fail);
                break;
            case SYNC_USER_INFO:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, R.string.sync_userinfo_api_fail);
                break;
            case GET_TOKEN:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, R.string.get_token_api_fail);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void goToMain() {
        editor.putString("loginToken", loginToken);
        editor.putString(SealConst.SEALTALK_LOGING_PHONE, phoneString);
        editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, passwordString);
        editor.apply();
        LoadDialog.dismiss(mContext);
        NToast.shortToast(mContext, R.string.login_success);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
