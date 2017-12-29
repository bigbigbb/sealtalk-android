package com.caishi.zhanghai.im.net;

import android.text.TextUtils;
import android.util.Log;

import com.caishi.zhanghai.im.bean.AuthBean;
import com.caishi.zhanghai.im.bean.HeartBean;
import com.caishi.zhanghai.im.utils.MD5;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

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

    public void initSocket(){
        try {
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
                    String message = msg;
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
                sendMsg(msg,classBack);
            }
        }).start();
    }
    public void sendMsg(String msg) {
        mClassBack = null;
        if (null != mSocket && null != mSocket.get()) {
            Socket soc = mSocket.get();
            try {
                if (!soc.isClosed() && !soc.isOutputShutdown()) {
                    OutputStream os = soc.getOutputStream();
                    String message = msg;
                    os.write(message.getBytes());
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            initSocket();
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

                            //收到服务器过来的消息，就通过Broadcast发送出去
//                            if (message.equals(HEART_BEAT_STRING)) {//处理心跳回复
//                                Intent intent = new Intent(HEART_BEAT_ACTION);
//                                mLocalBroadcastManager.sendBroadcast(intent);
//                            } else {
                            //其他消息回复
//                                Intent intent = new Intent(MESSAGE_ACTION);
//                                intent.putExtra("message", message);
//                                mLocalBroadcastManager.sendBroadcast(intent);
                        }
                    }
//                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}


