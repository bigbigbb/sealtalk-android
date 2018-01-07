package com.caishi.zhanghai.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import com.caishi.zhanghai.im.R;
import com.caishi.zhanghai.im.SealConst;
import com.caishi.zhanghai.im.bean.BaseReturnBean;
import com.caishi.zhanghai.im.bean.FriendAllBean;
import com.caishi.zhanghai.im.bean.FriendAllReturnBean;
import com.caishi.zhanghai.im.net.CallBackJson;
import com.caishi.zhanghai.im.net.SocketClient;
import com.caishi.zhanghai.im.server.broadcast.BroadcastManager;
import com.caishi.zhanghai.im.server.utils.NToast;
import com.caishi.zhanghai.im.server.widget.DialogWithYesOrNoUtils;
import com.caishi.zhanghai.im.ui.widget.switchbutton.SwitchButton;
import com.google.gson.Gson;

import io.rong.common.RLog;
import io.rong.imlib.RongIMClient;

/**
 * Created by AMing on 16/6/23.
 * Company RongCloud
 */
public class AccountSettingActivity extends BaseActivity implements View.OnClickListener {

    private boolean isDebug;
    private final static String TAG = "AccountSettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_set);
        isDebug = getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false);
        setTitle(R.string.account_setting);
        initViews();
    }

    private void initViews() {
        RelativeLayout mPassword = (RelativeLayout) findViewById(R.id.ac_set_change_pswd);
        RelativeLayout mPrivacy = (RelativeLayout) findViewById(R.id.ac_set_privacy);
        RelativeLayout mNewMessage = (RelativeLayout) findViewById(R.id.ac_set_new_message);
        RelativeLayout mClean = (RelativeLayout) findViewById(R.id.ac_set_clean);
        RelativeLayout mExit = (RelativeLayout) findViewById(R.id.ac_set_exit);
        LinearLayout layout_push = (LinearLayout) findViewById(R.id.layout_push_setting);

        if (isDebug) {
            layout_push.setVisibility(View.VISIBLE);
        } else {
            layout_push.setVisibility(View.GONE);
        }

        final SwitchButton mSwitchDetail = (SwitchButton) findViewById(R.id.switch_push_detail);

        RongIMClient.getInstance().getPushContentShowStatus(new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                mSwitchDetail.setChecked(aBoolean);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });


        mSwitchDetail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    RongIMClient.getInstance().setPushContentShowStatus(true, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            RLog.d(TAG, "set Push content success");
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            RLog.d(TAG, "set Push content failed errorCode = " + errorCode);
                        }
                    });
                } else {
                    RongIMClient.getInstance().setPushContentShowStatus(false, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            RLog.d(TAG, "set Push content success");
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            RLog.d(TAG, "set Push content failed errorCode = " + errorCode);
                        }
                    });
                }
            }
        });

        mPassword.setOnClickListener(this);
        mPrivacy.setOnClickListener(this);
        mNewMessage.setOnClickListener(this);
        mClean.setOnClickListener(this);
        mExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_set_change_pswd:
                startActivity(new Intent(this, UpdatePasswordActivity.class));
                break;
            case R.id.ac_set_privacy:
                startActivity(new Intent(this, PrivacyActivity.class));
                break;
            case R.id.ac_set_new_message:
                startActivity(new Intent(this, NewMessageRemindActivity.class));
                break;
            case R.id.ac_set_clean:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否清除缓存?", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void executeEvent() {
                        File file = new File(Environment.getExternalStorageDirectory().getPath() + getPackageName());
                        deleteFile(file);
                        NToast.shortToast(mContext, "清除成功");
                    }

                    @Override
                    public void executeEditEvent(String editText) {

                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });
                break;
            case R.id.ac_set_exit:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否退出登录?", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void executeEvent() {
                      logout();
                    }

                    @Override
                    public void executeEditEvent(String editText) {

                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });
                break;
        }
    }

    private void logout(){
        FriendAllBean friendAllBean = new FriendAllBean();
        friendAllBean.setK("logout");
        friendAllBean.setM("member");
        friendAllBean.setRid(String.valueOf(System.currentTimeMillis()));
        String msg = new Gson().toJson(friendAllBean);
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                Log.e("msg1111", json);
                BaseReturnBean baseReturnBean = new Gson().fromJson(json, BaseReturnBean.class);
                if (null != baseReturnBean) {
                    Message message = new Message();
                    message.obj = baseReturnBean;
                    handler.sendMessage(message);
                }


            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseReturnBean baseReturnBean   =  (BaseReturnBean) msg.obj;
            NToast.longToast(getApplication(),baseReturnBean.getDesc());
            if(baseReturnBean.getV().equals("ok")){
                BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.EXIT);
            }
        }
    };


    public void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

}
