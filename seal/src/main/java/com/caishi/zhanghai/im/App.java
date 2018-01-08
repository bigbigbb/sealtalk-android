package com.caishi.zhanghai.im;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.view.View;

import com.caishi.zhanghai.im.net.AppParm;
import com.caishi.zhanghai.im.net.GetUrlUtil;
import com.caishi.zhanghai.im.net.SocketClient;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.facebook.stetho.inspector.database.DefaultDatabaseConnectionProvider;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.contactcard.ContactCardExtensionModule;
import cn.rongcloud.contactcard.IContactCardClickListener;
import cn.rongcloud.contactcard.IContactCardInfoProvider;
import cn.rongcloud.contactcard.message.ContactMessage;
import com.caishi.zhanghai.im.db.Friend;
import com.caishi.zhanghai.im.message.TestMessage;
import com.caishi.zhanghai.im.message.provider.ContactNotificationMessageProvider;
import com.caishi.zhanghai.im.message.provider.TestMessageProvider;
import com.caishi.zhanghai.im.server.pinyin.CharacterParser;
import com.caishi.zhanghai.im.server.utils.NLog;
import com.caishi.zhanghai.im.server.utils.RongGenerate;
import com.caishi.zhanghai.im.stetho.RongDatabaseDriver;
import com.caishi.zhanghai.im.stetho.RongDatabaseFilesProvider;
import com.caishi.zhanghai.im.stetho.RongDbFilesDumperPlugin;
import com.caishi.zhanghai.im.ui.activity.UserDetailActivity;
import com.caishi.zhanghai.im.utils.SharedPreferencesContext;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.display.FadeInBitmapDisplayer;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.RealTimeLocationMessageProvider;
import io.rong.imlib.ipc.RongExceptionHandler;
import io.rong.imlib.model.UserInfo;
import io.rong.push.RongPushClient;
import io.rong.push.common.RongException;
import io.rong.recognizer.RecognizeExtensionModule;


public class App extends MultiDexApplication {

    private static DisplayImageOptions options;

    @Override
    public void onCreate() {

        super.onCreate();
//        initSocketNet();
        Stetho.initialize(new Stetho.Initializer(this) {
            @Override
            protected Iterable<DumperPlugin> getDumperPlugins() {
                return new Stetho.DefaultDumperPluginsBuilder(App.this)
                        .provide(new RongDbFilesDumperPlugin(App.this, new RongDatabaseFilesProvider(App.this)))
                        .finish();
            }

            @Override
            protected Iterable<ChromeDevtoolsDomain> getInspectorModules() {
                Stetho.DefaultInspectorModulesBuilder defaultInspectorModulesBuilder = new Stetho.DefaultInspectorModulesBuilder(App.this);
                defaultInspectorModulesBuilder.provideDatabaseDriver(new RongDatabaseDriver(App.this, new RongDatabaseFilesProvider(App.this), new DefaultDatabaseConnectionProvider()));
                return defaultInspectorModulesBuilder.finish();
            }
        });

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

//            LeakCanary.install(this);//内存泄露检测
            RongPushClient.registerHWPush(this);
            RongPushClient.registerMiPush(this, "2882303761517473625", "5451747338625");
            try {
                RongPushClient.registerFCM(this);
            } catch (RongException e) {
                e.printStackTrace();
            }

            /**
             * 注意：
             *
             * IMKit SDK调用第一步 初始化
             *
             * context上下文
             *
             * 只有两个进程需要初始化，主进程和 push 进程
             */
            RongIM.setServerInfo("nav.cn.ronghub.com", "up.qbox.me");
            RongIM.init(this);
            NLog.setDebug(true);//Seal Module Log 开关
            SealAppContext.init(this);
            SharedPreferencesContext.init(this);
            Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));

            try {
                RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
                RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());
                RongIM.registerMessageType(TestMessage.class);
                RongIM.registerMessageTemplate(new TestMessageProvider());


            } catch (Exception e) {
                e.printStackTrace();
            }

            openSealDBIfHasCachedToken();

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(com.caishi.zhanghai.im.R.drawable.de_default_portrait)
                    .showImageOnFail(com.caishi.zhanghai.im.R.drawable.de_default_portrait)
                    .showImageOnLoading(com.caishi.zhanghai.im.R.drawable.de_default_portrait)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();

//            RongExtensionManager.getInstance().registerExtensionModule(new PTTExtensionModule(this, true, 1000 * 60));
            RongExtensionManager.getInstance().registerExtensionModule(new ContactCardExtensionModule(new IContactCardInfoProvider() {
                @Override
                public void getContactAllInfoProvider(final IContactCardInfoCallback contactInfoCallback) {
                    SealUserInfoManager.getInstance().getFriends(new SealUserInfoManager.ResultCallback<List<Friend>>() {
                        @Override
                        public void onSuccess(List<Friend> friendList) {
                            contactInfoCallback.getContactCardInfoCallback(friendList);
                        }

                        @Override
                        public void onError(String errString) {
                            contactInfoCallback.getContactCardInfoCallback(null);
                        }
                    });
                }

                @Override
                public void getContactAppointedInfoProvider(String userId, String name, String portrait, final IContactCardInfoCallback contactInfoCallback) {
                    SealUserInfoManager.getInstance().getFriendByID(userId, new SealUserInfoManager.ResultCallback<Friend>() {
                        @Override
                        public void onSuccess(Friend friend) {
                            List<UserInfo> list = new ArrayList<>();
                            list.add(friend);
                            contactInfoCallback.getContactCardInfoCallback(list);
                        }

                        @Override
                        public void onError(String errString) {
                            contactInfoCallback.getContactCardInfoCallback(null);
                        }
                    });
                }

            }, new IContactCardClickListener() {
                @Override
                public void onContactCardClick(View view, ContactMessage content) {
                    Intent intent = new Intent(view.getContext(), UserDetailActivity.class);
                    Friend friend = SealUserInfoManager.getInstance().getFriendByID(content.getId());
                    if (friend == null) {
                        UserInfo userInfo = new UserInfo(content.getId(), content.getName(),
                                Uri.parse(TextUtils.isEmpty(content.getImgUrl()) ? RongGenerate.generateDefaultAvatar(content.getName(), content.getId()) : content.getImgUrl()));
                        friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                    }
                    intent.putExtra("friend", friend);
                    view.getContext().startActivity(intent);
                }
            }));
            RongExtensionManager.getInstance().registerExtensionModule(new RecognizeExtensionModule());


        }



    }

    private SocketClient mSocketClient;
    private void  initSocketNet(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetUrlUtil.requestGet();
                Looper.prepare();
                if(null!= AppParm.IP&&null!=AppParm.PORT){
                    mSocketClient = SocketClient.getInstance();
                    mSocketClient.initSocket(getApplicationContext());
                }
                Looper.loop();
            }
        }).start();


    }
    public static DisplayImageOptions getOptions() {
        return options;
    }

    private void openSealDBIfHasCachedToken() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String cachedToken = sp.getString("loginToken", "");
        if (!TextUtils.isEmpty(cachedToken)) {
            String current = getCurProcessName(this);
            String mainProcessName = getPackageName();
            if (mainProcessName.equals(current)) {
                SealUserInfoManager.getInstance().openDB();
            }
        }
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
