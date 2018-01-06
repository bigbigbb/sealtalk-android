package com.caishi.zhanghai.im.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.caishi.zhanghai.im.App;
import com.caishi.zhanghai.im.R;
import com.caishi.zhanghai.im.SealUserInfoManager;
import com.caishi.zhanghai.im.bean.BlackReturnBean;
import com.caishi.zhanghai.im.bean.FriendAllBean;
import com.caishi.zhanghai.im.bean.FriendAllReturnBean;
import com.caishi.zhanghai.im.net.CallBackJson;
import com.caishi.zhanghai.im.net.SocketClient;
import com.caishi.zhanghai.im.server.widget.LoadDialog;
import com.caishi.zhanghai.im.server.widget.SelectableRoundedImageView;
import com.caishi.zhanghai.im.ui.widget.SinglePopWindow;
import com.google.gson.Gson;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imlib.model.UserInfo;

public class BlackListActivity extends BaseActivity {

    private TextView isShowData;
    private ListView mBlackList;
    public static  BlackListActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_black);
        activity = this;
        setTitle(R.string.the_blacklist);
        initView();
        requestData();
    }

    private void requestData() {
        LoadDialog.show(mContext);
        getBlackList();


//        SealUserInfoManager.getInstance().getBlackList(new SealUserInfoManager.ResultCallback<List<UserInfo>>() {
//            @Override
//            public void onSuccess(List<UserInfo> userInfoList) {
//                LoadDialog.dismiss(mContext);
//                if (userInfoList != null) {
//                    if (userInfoList.size() > 0) {
//                        MyBlackListAdapter adapter = new MyBlackListAdapter(userInfoList);
//                        mBlackList.setAdapter(adapter);
//                    } else {
//                        isShowData.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//
//            @Override
//            public void onError(String errString) {
//                LoadDialog.dismiss(mContext);
//            }
//        });


        mBlackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SinglePopWindow morePopWindow = new SinglePopWindow(BlackListActivity.this, blacklistStatus.get(i).getId());
                morePopWindow.showPopupWindow(view);
            }
        });
    }

    public void  getBlackList(){
        FriendAllBean friendAllBean = new FriendAllBean();
        friendAllBean.setK("black_list");
        friendAllBean.setM("friend");
        friendAllBean.setRid(String.valueOf(System.currentTimeMillis()));
        String msg = new Gson().toJson(friendAllBean);
        SocketClient.getInstance().sendMessage(msg, new CallBackJson() {
            @Override
            public void returnJson(String json) {
                Log.e("msg1111",json);
                BlackReturnBean blackReturnBean = new Gson().fromJson(json,BlackReturnBean.class);
                if(null != blackReturnBean){
                    Message message = new Message();
                    message.obj = blackReturnBean;
                    message.what = 0;
                    handler.sendMessage(message);

                }
            }
        });

    }


    private List<BlackReturnBean.DataBean> blacklistStatus = new ArrayList<>();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoadDialog.dismiss(mContext);
            BlackReturnBean blackReturnBean  = (BlackReturnBean) msg.obj;
            if(null!=blackReturnBean){
                blacklistStatus = blackReturnBean.getData();

                if(null!=blacklistStatus){
                    if (blacklistStatus.size() > 0) {
                        MyBlackListAdapter adapter = new MyBlackListAdapter(blacklistStatus);
                        mBlackList.setAdapter(adapter);
                    } else {
                        mBlackList.setVisibility(View.GONE);
                        isShowData.setVisibility(View.VISIBLE);
                    }
                }else {
                    mBlackList.setVisibility(View.GONE);
                    isShowData.setVisibility(View.VISIBLE);
                }

            }
        }
    };
    private void initView() {
        isShowData = (TextView) findViewById(R.id.blacklsit_show_data);
        mBlackList = (ListView) findViewById(R.id.blacklsit_list);
    }

    class MyBlackListAdapter extends BaseAdapter {

        private List<BlackReturnBean.DataBean> userInfoList;

        public MyBlackListAdapter(List<BlackReturnBean.DataBean> dataList) {
            this.userInfoList = dataList;
        }

        @Override
        public int getCount() {
            return userInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return userInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            BlackReturnBean.DataBean userInfo = userInfoList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.black_item_new, parent, false);
                viewHolder.mName = (TextView) convertView.findViewById(R.id.blackname);
                viewHolder.mHead = (SelectableRoundedImageView) convertView.findViewById(R.id.blackuri);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mName.setText(userInfo.getNickname());
            ImageLoader.getInstance().displayImage(userInfo.getPortraitUri(), viewHolder.mHead, App.getOptions());
            return convertView;
        }


        class ViewHolder {
            SelectableRoundedImageView mHead;
            TextView mName;
        }
    }
}
