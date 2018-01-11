package com.caishi.zhanghai.im.net;

import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.caishi.zhanghai.im.SealAppContext;
import com.caishi.zhanghai.im.SealConst;
import com.caishi.zhanghai.im.bean.AuthBean;
import com.caishi.zhanghai.im.bean.HeartBean;
import com.caishi.zhanghai.im.bean.LoginBean;
import com.caishi.zhanghai.im.bean.LoginReturnBean;
import com.caishi.zhanghai.im.server.broadcast.BroadcastManager;
import com.caishi.zhanghai.im.server.utils.NToast;
import com.caishi.zhanghai.im.utils.MD5;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import io.rong.imkit.RongIM;

import static android.content.ContentValues.TAG;

/**
 * Created by shihui on 2017/12/15.
 */

public class SocketClient{
    public WeakReference<Socket> mSocket;
    public ReadThread mReadThread;
    public static SocketClient instance;
    CallBackJson mClassBack;
    public static synchronized SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }
        return instance;
    }

    public SocketClient(){

    }

    private Context mContext;
    public void initSocket(Context context){
        try {
            this.mContext = context;
            Socket so = new Socket(AppParm.IP, Integer.parseInt(AppParm.PORT));
            mSocket = new WeakReference<Socket>(so);
            mReadThread = new ReadThread(so);
            mReadThread.start();
            //准备链接认证
            sendAuth();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAuth(){
        String time = String.valueOf(System.currentTimeMillis());
        AuthBean authBean = new AuthBean();
        authBean.setK("auth");
        authBean.setRid(time);
        authBean.setM("system");
        String v = "Android-"+ MD5.getStringMD5("Android|ZhanghaiAPP4AndroidPass|1.0.0")+"-1.0.0";
        authBean.setV(v);
        sendMsg(new Gson().toJson(authBean));

    }


    private String initHeartData(){
        //{"rid":"xxx","m":"system","k":"pong","v":"客户端时间戳"}
        String time = String.valueOf(System.currentTimeMillis());
        HeartBean heartBean = new HeartBean();
        heartBean.setK("pong");
        heartBean.setM("system");
        heartBean.setRid(time);
        heartBean.setV(time);
        return  new Gson().toJson(heartBean);
    }

    public void  sendMsg(String msg, CallBackJson classBack){
        this.mClassBack = classBack;
        if (null != mSocket && null != mSocket.get()) {
            Socket soc = mSocket.get();
            try {
                if (!soc.isClosed() && !soc.isOutputShutdown()) {
                    OutputStream os = soc.getOutputStream();
                    String message = msg+"$~ZHANGHAI-END-POINT~$";
                    os.write(message.getBytes());
                    os.flush();
                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }





    public void sendMessage(final String msg, final CallBackJson classBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager connectivityManager =  (ConnectivityManager) mContext
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getActiveNetworkInfo() != null) {
                    sendMsg(msg,classBack);
                }else {
                    Looper.prepare();
                    Toast.makeText(mContext,"网络请求失败，请检查您的网络设置",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }

            }
        }).start();
    }


    public void sendMsg(String msg) {
        ConnectivityManager connectivityManager =  (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null) {
            mClassBack = null;
            if (null != mSocket && null != mSocket.get()) {
                Socket soc = mSocket.get();
                try {
                    if (!soc.isClosed() && !soc.isOutputShutdown()) {
                        OutputStream os = soc.getOutputStream();
                        String message = msg+"$~ZHANGHAI-END-POINT~$";
                        os.write(message.getBytes());
                        os.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext,"网络请求失败，请检查您的网络设置",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }).start();
        }


    }
    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
//            initSocket();
        }
    }
    public void releaseLastSocket(WeakReference<Socket> mSocket) {
        try {
            if (null != mSocket) {
                Socket sk = mSocket.get();
                if (!sk.isClosed()) {
                    sk.close();
                }
                sk = null;
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class ReadThread extends Thread {
        private WeakReference<Socket> mWeakSocket;
        private boolean isStart = true;

        public ReadThread(Socket socket) {
            mWeakSocket = new WeakReference<Socket>(socket);
        }

        public void release() {
            isStart = false;
            releaseLastSocket(mWeakSocket);
        }

        @Override
        public void run() {
            super.run();
            Socket socket = mWeakSocket.get();
            if (null != socket) {
                Log.e("test","会进来嘛？");
                try {
                    InputStream is = socket.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int length = 0;


                    while (!socket.isClosed() && !socket.isInputShutdown()
                            && isStart && ((length = is.read(buffer)) != -1)) {
                        if (length > 0) {
                            String message = new String(Arrays.copyOf(buffer,
                                    length)).trim();
                            Log.e(TAG, message);
                            if(!TextUtils.isEmpty(message)&&message.equals("{\"rid\":\"0\",\"m\":\"system\",\"k\":\"ping\",\"v\":\"\"}")){
                                Log.e("test","收到心跳检测");
                                sendMsg(initHeartData());

                            }else if(null!=mClassBack){
                                mClassBack.returnJson(message);

                            }
                        }
                    }
                    Log.e("test","断了");
                    releaseLastSocket(mSocket);
                    BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.BREAK_UP);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test","断了");
                    releaseLastSocket(mSocket);
                    BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.BREAK_UP);
                }
            }

        }

    }


}


