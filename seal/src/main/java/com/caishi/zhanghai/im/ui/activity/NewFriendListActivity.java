package com.caishi.zhanghai.im.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.caishi.zhanghai.im.R;
import com.caishi.zhanghai.im.SealAppContext;
import com.caishi.zhanghai.im.SealUserInfoManager;
import com.caishi.zhanghai.im.bean.AddFriendReturnBean;
import com.caishi.zhanghai.im.bean.AgreeFriendBean;
import com.caishi.zhanghai.im.bean.AgreeFriendReturnBean;
import com.caishi.zhanghai.im.bean.FriendAllBean;
import com.caishi.zhanghai.im.bean.FriendAllReturnBean;
import com.caishi.zhanghai.im.db.Friend;
import com.caishi.zhanghai.im.net.CallBackJson;
import com.caishi.zhanghai.im.net.SocketClient;
import com.caishi.zhanghai.im.server.broadcast.BroadcastManager;
import com.caishi.zhanghai.im.server.network.http.HttpException;
import com.caishi.zhanghai.im.server.pinyin.CharacterParser;
import com.caishi.zhanghai.im.server.response.AgreeFriendsResponse;
import com.caishi.zhanghai.im.server.response.UserRelationshipResponse;
import com.caishi.zhanghai.im.server.utils.CommonUtils;
import com.caishi.zhanghai.im.server.utils.NToast;
import com.caishi.zhanghai.im.server.widget.LoadDialog;
import com.caishi.zhanghai.im.ui.adapter.NewFriendListAdapter;
import com.google.gson.Gson;


public class NewFriendListActivity extends BaseActivity implements NewFriendListAdapter.OnItemButtonClick, View.OnClickListener {

    private static final int GET_ALL = 11;
    private static final int AGREE_FRIENDS = 12;
    public static final int FRIEND_LIST_REQUEST_CODE = 1001;
    private ListView shipListView;
    private NewFriendListAdapter adapter;
    private String friendId;
    private TextView isData;
    private UserRelationshipResponse userRelationshipResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friendlist);
        initView();
        if (!CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, R.string.check_network);
            return;
        }
        LoadDialog.show(mContext);
//        request(GET_ALL);
        getAllFriendShip();
        adapter = new NewFriendListAdapter(mContext);
        shipListView.setAdapter(adapter);
    }

    protected void initView() {
        setTitle(R.string.new_friends);
        shipListView = (ListView) findViewById(R.id.shiplistview);
        isData = (TextView) findViewById(R.id.isData);
        Button rightButton = getHeadRightButton();
        rightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.de_address_new_friend));
        rightButton.setOnClickListener(this);


    }


    private  void   getAllFriendShip(){
        FriendAllBean friendAllBean = new FriendAllBean();
        friendAllBean.setK("all");
        friendAllBean.setM("friend");
        friendAllBean.setRid(String.valueOf(System.currentTimeMillis()));
        String msg = new Gson().toJson(friendAllBean);
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                                                                                                                                                                                                                                                                                                                     Log.e("msg1111",json);
                FriendAllReturnBean  friendAllReturnBean = new Gson().fromJson(json,FriendAllReturnBean.class);
                if(null != friendAllReturnBean){
                    Message message = new Message();
                    message.obj = friendAllReturnBean;
                    message.what = 0;
                    handler.sendMessage(message);

                }
            }
        });

    }


    private FriendAllReturnBean friendAllReturnBean;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    friendAllReturnBean  = (FriendAllReturnBean) msg.obj;
                    List<FriendAllReturnBean.DataBean>  dataBeanList  = friendAllReturnBean.getData();
                    if(null!=dataBeanList){
                        if (dataBeanList.size() == 0) {
                            isData.setVisibility(View.VISIBLE);
                            LoadDialog.dismiss(mContext);
                            return;
                        }

                        Collections.sort(dataBeanList, new Comparator<FriendAllReturnBean.DataBean>() {

                            @Override
                            public int compare(FriendAllReturnBean.DataBean lhs,FriendAllReturnBean.DataBean rhs) {
                                Date date1 = stringToDate(lhs);
                                Date date2 = stringToDate(rhs);
                                if (date1.before(date2)) {
                                    return 1;
                                }
                                return -1;
                            }
                        });

                        adapter.removeAll();
                        adapter.addData(dataBeanList);

                        adapter.notifyDataSetChanged();
                        adapter.setOnItemButtonClick(NewFriendListActivity.this);
                    }

                    LoadDialog.dismiss(mContext);
                    break;
                case 1:
                    AgreeFriendReturnBean  agreeFriendReturnBean  =  (AgreeFriendReturnBean) msg.obj;
                    if(agreeFriendReturnBean.getV().equals("ok")){
                        FriendAllReturnBean.DataBean bean = friendAllReturnBean.getData().get(index);
                        SealUserInfoManager.getInstance().addFriend(new Friend(bean.getUser().getId(),
                                bean.getUser().getNickname(),
                                Uri.parse(bean.getUser().getPortraitUri()),
                                bean.getDisplayName(),
                                String.valueOf(bean.getStatus()),
                                null,
                                null,
                                null,
                                CharacterParser.getInstance().getSpelling(bean.getUser().getNickname()),
                                CharacterParser.getInstance().getSpelling(bean.getDisplayName())));
                        // 通知好友列表刷新数据
                        NToast.shortToast(mContext, R.string.agreed_friend);
                        LoadDialog.dismiss(mContext);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
//                        request(GET_ALL); //刷新 UI 按钮
                        getAllFriendShip(); //刷新 UI 按钮
                    }
                    break;
            }

        }
    };

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case GET_ALL:
                return action.getAllUserRelationship();
            case AGREE_FRIENDS:
                return action.agreeFriends(friendId);
        }
        return super.doInBackground(requestCode, id);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case GET_ALL:
                    userRelationshipResponse = (UserRelationshipResponse) result;

                    if (userRelationshipResponse.getResult().size() == 0) {
                        isData.setVisibility(View.VISIBLE);
                        LoadDialog.dismiss(mContext);
                        return;
                    }

                    Collections.sort(userRelationshipResponse.getResult(), new Comparator<UserRelationshipResponse.ResultEntity>() {

                        @Override
                        public int compare(UserRelationshipResponse.ResultEntity lhs, UserRelationshipResponse.ResultEntity rhs) {
                            Date date1 = stringToDate(lhs);
                            Date date2 = stringToDate(rhs);
                            if (date1.before(date2)) {
                                return 1;
                            }
                            return -1;
                        }
                    });

                    adapter.removeAll();
                    adapter.addData(userRelationshipResponse.getResult());

                    adapter.notifyDataSetChanged();
                    adapter.setOnItemButtonClick(this);
                    LoadDialog.dismiss(mContext);
                    break;
                case AGREE_FRIENDS:
                    AgreeFriendsResponse afres = (AgreeFriendsResponse) result;
                    if (afres.getCode() == 200) {
                        UserRelationshipResponse.ResultEntity bean = userRelationshipResponse.getResult().get(index);
                        SealUserInfoManager.getInstance().addFriend(new Friend(bean.getUser().getId(),
                                bean.getUser().getNickname(),
                                Uri.parse(bean.getUser().getPortraitUri()),
                                bean.getDisplayName(),
                                String.valueOf(bean.getStatus()),
                                null,
                                null,
                                null,
                                CharacterParser.getInstance().getSpelling(bean.getUser().getNickname()),
                                CharacterParser.getInstance().getSpelling(bean.getDisplayName())));
                        // 通知好友列表刷新数据
                        NToast.shortToast(mContext, R.string.agreed_friend);
                        LoadDialog.dismiss(mContext);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
                        request(GET_ALL); //刷新 UI 按钮
                    }

            }
        }
    }


    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case GET_ALL:
                break;

        }
    }


    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter = null;
        }
        super.onDestroy();
    }

    private int index;

    @Override
    public boolean onButtonClick(int position, View view, int status) {
        index = position;
        switch (status) {
            case 11: //收到了好友邀请
                if (!CommonUtils.isNetworkConnected(mContext)) {
                    NToast.shortToast(mContext, R.string.check_network);
                    break;
                }
                LoadDialog.show(mContext);
//                friendId = null;
//                friendId = userRelationshipResponse.getResult().get(position).getUser().getId();
//                request(AGREE_FRIENDS);
                friendId = friendAllReturnBean.getData().get(position).getUser().getId();
                agreeFriend();
                break;
            case 10: // 发出了好友邀请
                break;
            case 21: // 忽略好友邀请
                break;
            case 20: // 已是好友
                break;
            case 30: // 删除了好友关系
                break;
        }
        return false;
    }


    private void agreeFriend(){
        AgreeFriendBean agreeFriendBean = new AgreeFriendBean();
        agreeFriendBean.setK("agree");
        agreeFriendBean.setM("friend");
        agreeFriendBean.setRid(String.valueOf(System.currentTimeMillis()));
        AgreeFriendBean.VBean vBean = new AgreeFriendBean.VBean();
        vBean.setFriendId(friendId);
        agreeFriendBean.setV(vBean);
        String msg = new Gson().toJson(agreeFriendBean);
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                AgreeFriendReturnBean agreeFriendReturnBean  = new Gson().fromJson(json,AgreeFriendReturnBean.class);
                if(null!=agreeFriendReturnBean){
                    Message message = new Message();
                    message.obj = agreeFriendReturnBean;
                    message.what = 1;
                    handler.sendMessage(message);
                }

            }
        });

    }
    private Date stringToDate(UserRelationshipResponse.ResultEntity resultEntity) {
        String updatedAt = resultEntity.getUpdatedAt();
        String updatedAtDateStr = updatedAt.substring(0, 10) + " " + updatedAt.substring(11, 16);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date updateAtDate = null;
        try {
            updateAtDate = simpleDateFormat.parse(updatedAtDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return updateAtDate;
    }

    private Date stringToDate(FriendAllReturnBean.DataBean rhs) {
        String updatedAt = rhs.getUpdatedAt();
        String updatedAtDateStr = updatedAt.substring(0, 10) + " " + updatedAt.substring(11, 16);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date updateAtDate = null;
        try {
            updateAtDate = simpleDateFormat.parse(updatedAtDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return updateAtDate;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(NewFriendListActivity.this, SearchFriendActivity.class);
        startActivityForResult(intent, FRIEND_LIST_REQUEST_CODE);
    }
}
