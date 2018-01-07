/**
 * Copyright (C) 2015 Luki(liulongke@gmail.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 　　　　http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apm29.kotlinapp.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;


/**
 * XWebView
 * Created by Luki on 2015/7/7.
 * Version:1
 */
public class XWebView extends WebView {

    private Context mContext;
    private String mData;
    private boolean m_bClearHistory;


    public XWebView(Context context) {
        this(context, null);
    }


    public XWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public XWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        m_bClearHistory = true;
        init();
    }

    public boolean needClearHistory() {
        return m_bClearHistory;
    }

    public void setClearHistoryFlag(boolean bClearHistory) {
        m_bClearHistory = bClearHistory;
    }

    @Override
    public void loadUrl(String url) {
        //String tokenUrl = getUrlWithToken(url);
        //String versionTokenUrl = getUrlWithVersion(tokenUrl);
        super.loadUrl(url);
    }


    /**
     * 分享的load
     *
     * @param url
     */
    public void loadJsCallBack(String url) {
        super.loadUrl(url);
    }

    @SuppressWarnings({"SetJavaScriptEnabled", "addJavascriptInterface", "deprecation"})
    private void init() {
        setScrollbarFadingEnabled(true);
        setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        final WebSettings webSettings = getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 让网页自适应屏幕宽度
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        setWebViewClient(new XWebViewClient(this));
        setWebChromeClient(new XWebChromeClient(this));
    }




    public static class XWebViewClient extends WebViewClient {

        private final XWebView xWebView;

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        public XWebViewClient(XWebView xWebView) {
            this.xWebView = xWebView;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //			view.loadUrl(url);
            //			view.requestFocus();
            //			return true;
            if(url.startsWith("tel")) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("当前界面无有效证书,是否继续访问？");
            builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            xWebView.getSettings().setBlockNetworkImage(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(xWebView != null && xWebView.needClearHistory()) {
                xWebView.setClearHistoryFlag(false);
                xWebView.clearHistory();
            }
            xWebView.getSettings().setBlockNetworkImage(false);
        }

    }



    public static class XWebChromeClient extends WebChromeClient {

        private final XWebView xWebView;


        public XWebChromeClient(XWebView xWebView) {
            this.xWebView = xWebView;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }



        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(xWebView.mContext);
            builder.setTitle(message);

            final EditText editor = new EditText(xWebView.mContext);
            builder.setView(editor);
            builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm(editor.getText().toString());
                }
            }).setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }
    }

}
