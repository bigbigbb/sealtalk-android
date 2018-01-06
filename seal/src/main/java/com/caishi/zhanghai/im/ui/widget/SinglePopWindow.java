package com.caishi.zhanghai.im.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.zhanghai.im.R;
import com.caishi.zhanghai.im.SealUserInfoManager;
import com.caishi.zhanghai.im.bean.AgreeFriendBean;
import com.caishi.zhanghai.im.bean.AgreeFriendReturnBean;
import com.caishi.zhanghai.im.db.BlackList;
import com.caishi.zhanghai.im.db.Friend;
import com.caishi.zhanghai.im.net.CallBackJson;
import com.caishi.zhanghai.im.net.SocketClient;
import com.caishi.zhanghai.im.server.SealAction;
import com.caishi.zhanghai.im.server.network.async.AsyncTaskManager;
import com.caishi.zhanghai.im.server.network.async.OnDataListener;
import com.caishi.zhanghai.im.server.network.http.HttpException;
import com.caishi.zhanghai.im.server.utils.NToast;
import com.caishi.zhanghai.im.ui.activity.BlackListActivity;
import com.google.gson.Gson;

import io.rong.imkit.RongIM;
import io.rong.imkit.utilities.PromptPopupDialog;
import io.rong.imlib.RongIMClient;

/**
 * Created by AMing on 16/8/1.
 * Company RongCloud
 */
public class SinglePopWindow extends PopupWindow {
    private static final int ADDBLACKLIST = 167;
    private static final int REMOVEBLACKLIST = 168;
    private View conentView;
    private AsyncTaskManager asyncTaskManager;
    private Context context;

    private Friend friend;

    @SuppressLint("InflateParams")
    public SinglePopWindow(final Activity context, final Friend friend, final RongIMClient.BlacklistStatus blacklistStatus) {
        this.friend = friend;
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                                  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popupwindow_more, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        asyncTaskManager = AsyncTaskManager.getInstance(context);
        RelativeLayout blacklistStatusRL = (RelativeLayout) conentView.findViewById(R.id.blacklist_status);
        final TextView blacklistText = (TextView) conentView.findViewById(R.id.blacklist_text_status);

        if (blacklistStatus == RongIMClient.BlacklistStatus.IN_BLACK_LIST) {
            blacklistText.setText("移除黑名单");
        } else {
            blacklistText.setText("加入黑名单");
        }



        blacklistStatusRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (blacklistStatus == RongIMClient.BlacklistStatus.IN_BLACK_LIST) {
                    RongIM.getInstance().removeFromBlacklist(friend.getUserId(), new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
//                            asyncTaskManager.request(ADDBLACKLIST, new OnDataListener() {
//                                @Override
//                                public Object doInBackground(int requestCode, String parameter) throws HttpException {
//                                    return new SealAction(context).removeFromBlackList(friend.getUserId());
//                                }
//
//                                @Override
//                                public void onSuccess(int requestCode, Object result) {
//                                    SealUserInfoManager.getInstance().deleteBlackList(friend.getUserId());
//                                    NToast.shortToast(context, "移除成功");
//                                }
//
//                                @Override
//                                public void onFailure(int requestCode, int state, Object result) {
//
//                                }
//                            });
                            addBlack(2);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            NToast.shortToast(context, "移除失败");
                        }
                    });
                } else {
                    PromptPopupDialog.newInstance(context, context.getString(R.string.join_the_blacklist),
                    context.getString(R.string.des_add_friend_to_black_list)).setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            RongIM.getInstance().addToBlacklist(friend.getUserId(), new RongIMClient.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    addBlack(1);

//                                    asyncTaskManager.request(REMOVEBLACKLIST, new OnDataListener() {
//                                        @Override
//                                        public Object doInBackground(int requestCode, String parameter) throws HttpException {
//                                            return new SealAction(context).addToBlackList(friend.getUserId());
//                                        }
//
//                                        @Override
//                                        public void onSuccess(int requestCode, Object result) {
//                                            SealUserInfoManager.getInstance().addBlackList(new BlackList(
//                                                        friend.getUserId(),
//                                                        null,
//                                                        null
//                                                    ));
//                                            NToast.shortToast(context, "加入成功");
//                                        }
//
//                                        @Override
//                                        public void onFailure(int requestCode, int state, Object result) {
//
//                                        }
//                                    });
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    NToast.shortToast(context, "加入失败");
                                }
                            });
                        }
                    }).show();
                }
                SinglePopWindow.this.dismiss();
            }

        });

    }

    private String friendId;
    @SuppressLint("InflateParams")
    public SinglePopWindow(final Activity context, final String friendId) {
        this.friendId = friendId;
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popupwindow_more, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        asyncTaskManager = AsyncTaskManager.getInstance(context);
        RelativeLayout blacklistStatusRL = (RelativeLayout) conentView.findViewById(R.id.blacklist_status);
        TextView blacklistText = (TextView) conentView.findViewById(R.id.blacklist_text_status);
        blacklistText.setText("移除黑名单");




        blacklistStatusRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    RongIM.getInstance().removeFromBlacklist(friendId, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {

                            addBlack(2);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            NToast.shortToast(context, "移除失败");
                        }
                    });


                SinglePopWindow.this.dismiss();
            }

        });

    }


    private void  addBlack(final int type){
        AgreeFriendBean agreeFriendBean = new AgreeFriendBean();
        if(type==1){//拉黑
            agreeFriendBean.setK("add_black");
        }else if(type==2){//拉白
            agreeFriendBean.setK("del_black");
        }

        agreeFriendBean.setM("friend");
        agreeFriendBean.setRid(String.valueOf(System.currentTimeMillis()));
        AgreeFriendBean.VBean vBean = new AgreeFriendBean.VBean();
        if(null!=friend){
            vBean.setFriendId(friend.getUserId());
        }else {
            vBean.setFriendId(friendId);
        }

        agreeFriendBean.setV(vBean);
        String msg = new Gson().toJson(agreeFriendBean);
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                AgreeFriendReturnBean agreeFriendReturnBean  = new Gson().fromJson(json,AgreeFriendReturnBean.class);
                if(null!=agreeFriendReturnBean){
                    Message message = new Message();
                    message.obj = agreeFriendReturnBean;
                    if(type == 1){
                        message.what = 1;
                    }else if(type==2) {//拉白
                        message.what = 2;
                    }

                    handler.sendMessage(message);
                }

            }
        });

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AgreeFriendReturnBean agreeFriendReturnBean  = (AgreeFriendReturnBean) msg.obj;
            NToast.shortToast(context, agreeFriendReturnBean.getDesc());
            if(agreeFriendReturnBean.getV().equals("ok")){
                switch (msg.what){
                    case 1://黑
                        SealUserInfoManager.getInstance().addBlackList(new BlackList(
                                friend.getUserId(),
                                null,
                                null
                        ));

                        break;
                    case 2://白
                        if(null!=friend){
                            SealUserInfoManager.getInstance().deleteBlackList(friend.getUserId());
                        }else {
                            SealUserInfoManager.getInstance().deleteBlackList(friendId);
                            BlackListActivity.activity.getBlackList();
                        }

                        break;
                }
            }

        }
    };
    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }
}
