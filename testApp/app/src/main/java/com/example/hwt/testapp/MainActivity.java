package com.example.hwt.testapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.app.AlertDialog.Builder;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Intent.URI_INTENT_SCHEME;

public class MainActivity extends AppCompatActivity {

    RelativeLayout container;

    RingProgressBar progressBar;

    private WebChromeClient webChromeClient = new WebChromeClient() {
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            Builder localBuilder = new Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();
            result.confirm();
            return true;
        }

        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("网页标题:");
            stringBuilder.append(title);
        }

        public void onProgressChanged(WebView view, int newProgress) {
        }
    };
    WebView webView;
    private WebViewClient webViewClient = new WebViewClient() {
        public void onPageFinished(WebView view, String url) {
            MainActivity.this.webView.getSettings().setBlockNetworkImage(false);
            super.onPageFinished(view, url);
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("alipays:") || url.contains("alipay") || url.contains("alipay.com")) {
                try {
                    MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                } catch (Exception e) {
                    new Builder(MainActivity.this).setMessage("未检测到支付宝客户端，请安装后重试。").setPositiveButton("立即安装", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://d.alipay.com")));
                        }
                    }).setNegativeButton("取消", null).show();
                }
                return true;
            } else if (!url.equals("http://www.google.com/")) {
                return super.shouldOverrideUrlLoading(view, url);
            } else {
                Toast.makeText(MainActivity.this, "国内不能访问google,拦截该url", Toast.LENGTH_LONG).show();
                return true;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.activity_main);
        this.webView = (WebView) findViewById(R.id.webView);
        this.container = (RelativeLayout) findViewById(R.id.container);
        this.progressBar = (RingProgressBar) findViewById(R.id.progressbar);
        ((TextView) findViewById(R.id.name)).setText(getString(R.string.app_name) + "加载中...");
        ((TextView) findViewById(R.id.companyname)).setText("由"+ getString(R.string.company_name)+ "提供");
        initData();
    }

    private void initData() {
        this.webView.setWebChromeClient(this.webChromeClient);
        this.webView.setWebViewClient(this.webViewClient);
        this.webView.getSettings().setBlockNetworkImage(true);
        this.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setSupportZoom(true);
        this.webView.getSettings().setBuiltInZoomControls(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setAppCacheEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.loadUrl("http://h.4399.com/play/195683.htm");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mHandler.sendEmptyMessage(1);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what >= 100) {
                container.setVisibility(View.GONE);
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                mHandler.sendEmptyMessageDelayed(msg.what + 3, 300L);
                progressBar.setProgress(msg.what);
            }
        }
    };

    private void startAlipayActivity(String url) {
        try {
            Intent intent = Intent.parseUri(url, URI_INTENT_SCHEME);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setComponent(null);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode != 4 || !this.webView.canGoBack()) {
//            return super.onKeyDown(keyCode, event);
//        }
//        this.webView.goBack();
//        return true;
//    }

}
