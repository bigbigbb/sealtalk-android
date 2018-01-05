package com.caishi.zhanghai.im.ui.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.caishi.zhanghai.im.R;
import com.caishi.zhanghai.im.SealConst;
import com.caishi.zhanghai.im.bean.BaseReturnBean;
import com.caishi.zhanghai.im.bean.SetNickNameBean;
import com.caishi.zhanghai.im.net.CallBackJson;
import com.caishi.zhanghai.im.net.SocketClient;
import com.caishi.zhanghai.im.server.broadcast.BroadcastManager;
import com.caishi.zhanghai.im.server.network.http.HttpException;
import com.caishi.zhanghai.im.server.response.SetNameResponse;
import com.caishi.zhanghai.im.server.utils.NToast;
import com.caishi.zhanghai.im.server.widget.ClearWriteEditText;
import com.caishi.zhanghai.im.server.widget.LoadDialog;
import com.google.gson.Gson;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/6/23.
 * Company RongCloud
 */
public class UpdateNameActivity extends BaseActivity implements View.OnClickListener {

    private static final int UPDATE_NAME = 7;
    private ClearWriteEditText mNameEditText;
    private String newName;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_name);
        setTitle(getString(R.string.update_name));
        Button rightButton = getHeadRightButton();
        rightButton.setVisibility(View.GONE);
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText(getString(R.string.confirm));
        mHeadRightText.setOnClickListener(this);
        mNameEditText = (ClearWriteEditText) findViewById(R.id.update_name);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        mNameEditText.setText(sp.getString(SealConst.SEALTALK_LOGIN_NAME, ""));
        mNameEditText.setSelection(sp.getString(SealConst.SEALTALK_LOGIN_NAME, "").length());
        editor = sp.edit();

    }


    private void  setNickName(){
        final SetNickNameBean setNickNameBean = new SetNickNameBean();
        setNickNameBean.setK("set_nickname");
        setNickNameBean.setM("member");
        setNickNameBean.setRid(String.valueOf(System.currentTimeMillis()));
        SetNickNameBean.VBean vBean = new SetNickNameBean.VBean();
        vBean.setNickname(newName);
        setNickNameBean.setV(vBean);
        String msg = new Gson().toJson(setNickNameBean);
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                BaseReturnBean setNickNameReturnBean  = new Gson().fromJson(json,BaseReturnBean.class);
                Message message = new Message();
                message.obj = setNickNameReturnBean;
                handler.sendMessage(message);
            }
        });

    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseReturnBean  setNickNameReturnBean =  (BaseReturnBean)msg.obj;
            NToast.shortToast(mContext, setNickNameReturnBean.getDesc());
            if(setNickNameReturnBean.getV().equals("ok")){
                editor.putString(SealConst.SEALTALK_LOGIN_NAME, newName);
                editor.commit();

                BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.CHANGEINFO);

                RongIM.getInstance().refreshUserInfoCache(new UserInfo(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""), newName, Uri.parse(sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, ""))));
                RongIM.getInstance().setCurrentUserInfo(new UserInfo(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""), newName, Uri.parse(sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, ""))));

                LoadDialog.dismiss(mContext);

                finish();
            }

        }
    };

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        return action.setName(newName);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        SetNameResponse sRes = (SetNameResponse) result;
        if (sRes.getCode() == 200) {
            editor.putString(SealConst.SEALTALK_LOGIN_NAME, newName);
            editor.commit();

            BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.CHANGEINFO);

            RongIM.getInstance().refreshUserInfoCache(new UserInfo(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""), newName, Uri.parse(sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, ""))));
            RongIM.getInstance().setCurrentUserInfo(new UserInfo(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""), newName, Uri.parse(sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, ""))));

            LoadDialog.dismiss(mContext);
            NToast.shortToast(mContext, "昵称更改成功");
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        newName = mNameEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(newName)) {
            LoadDialog.show(mContext);
//            request(UPDATE_NAME, true);
            setNickName();
        } else {
            NToast.shortToast(mContext, "昵称不能为空");
            mNameEditText.setShakeAnimation();
        }
    }
}
