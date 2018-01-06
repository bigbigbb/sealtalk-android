package com.caishi.zhanghai.im.ui.fragment;

import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.util.ArrayList;

import com.caishi.zhanghai.im.R;
import com.caishi.zhanghai.im.server.SealAction;
import com.caishi.zhanghai.im.server.network.async.AsyncTaskManager;
import com.caishi.zhanghai.im.server.network.async.OnDataListener;
import com.caishi.zhanghai.im.server.network.http.HttpException;
import com.caishi.zhanghai.im.server.response.DefaultConversationResponse;
import com.caishi.zhanghai.im.server.utils.NToast;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;


public class DiscoverFragment extends Fragment {


    private WebView mWebView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mWebView = (WebView) view.findViewById(R.id.discover_webview);

        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setDefaultFontSize(18);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.loadUrl("https://www.kancloud.cn/futao/lvchat?token=G7KWALpCsL");

        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });




    }


}
