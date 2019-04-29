package com.corydon.miu;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;


public class WebsiteFragment extends Fragment {

    String url;
    FrameLayout web_container;
    WebView webView;

    public static WebsiteFragment NewInstance(String url) {
        WebsiteFragment websiteFragment = new WebsiteFragment();
        Bundle args =  new Bundle();
        args.putString("URL", url);
        websiteFragment.setArguments(args);
        return websiteFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (null != args)
        {
            url = args.getString("URL");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.website_fragment, container, false);

        web_container = view.findViewById(R.id.web_container);
        webView = new WebView(getActivity().getApplicationContext());
        web_container.addView(webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 从 LOLLIPOP（安卓5.0） 开始 webveiw 默认不允许混合模式，https 中不能加载 http 资源
            // 在聚橙网中，图片资源均是 http 引用资源，需要开启混合模式
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        webView.goBack();   // 在 webview 中返回上一页
                        return true;
                    }
                }
                return false;
            }
        });

        webView.loadUrl(url);

        return view;
    }


    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        web_container.removeAllViews();     //  释放 webview 占用的内存防止 OOM，实际效果好像还可以
        webView.destroy();
    }
}
