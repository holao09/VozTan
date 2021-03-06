package com.SuperNamek.NextVoz.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.SuperNamek.NextVoz.CustomWebView;
import com.SuperNamek.NextVoz.Downloader;
import com.SuperNamek.NextVoz.MainActivity;
import com.SuperNamek.NextVoz.R;
import com.SuperNamek.NextVoz.adblock.AdBlockWebViewClient;
import com.SuperNamek.NextVoz.events.EventRedirectBrowser;

import java.io.InputStream;

/**
 * @Author: SuperNamek
 */

public class BrowserFragment extends Fragment {

    public static final String TAG = "WEBVIEW_FRAGMENT";

    private static CustomWebView webView;
    private WebSettings webSettings;

    private AdBlockWebViewClient adBlockWebViewClient = new AdBlockWebViewClient(true)
    {
        @Override
        public void onPageFinished(WebView view, String url) {

            // Inject CSS when page is done loading
            injectCSS();
            super.onPageFinished(view, url);
        }
    };

    private void injectCSS() {
        try {
            InputStream inputStream;
            if(getAdblock_status()) {
                inputStream = getContext().getAssets().open("blockads.css");
            }
            else
                {
                    inputStream = getContext().getAssets().open("unblockads.css"); ;
                }
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAdblock_status(boolean status)
    {
        adBlockWebViewClient.setmAdBlockEnabled(status);
    }

    public boolean getAdblock_status()
    {
        return adBlockWebViewClient.getmAdBlockEnabled();
    }
    public BrowserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRedirectBrowser(EventRedirectBrowser event) {
        webView.loadUrl(event.getUrl());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);

        webView = (CustomWebView)view.findViewById(R.id.webview);

        webView.setGestureDetector(new GestureDetector(new CustomeGestureDetector()));

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            initWebView();
        }

        webView.loadUrl(getString(R.string.URL_WEBSITE));
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        webView.saveState(savedInstanceState);
    }

    public CustomWebView getWebView() {
        return this.webView;
    }

    private void initWebView() {
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // Enable Javascript
        webView.getSettings().setJavaScriptEnabled(true);

        webView.clearCache(true);
        webView.setWebChromeClient(new WebChromeClient());
        //webView.setWebViewClient(new CustomizedWebViewClient());
        webView.setWebViewClient(adBlockWebViewClient);
        webView.setDownloadListener(new Downloader(getActivity()));
    }
    public class CustomeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            float displayheight = displayMetrics.ydpi;
            if(e1 == null || e2 == null) return false;
            if(e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
            else {

                try {
                    if(e1.getY() - e2.getY() > 35*displayheight/100 ) {
                        // Hide Actionbar
                        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
                        MainActivity.getInstance().hideSystemUI();
                        webView.invalidate();
                        return false;
                    }
                    else if (e2.getY() - e1.getY() > 35*displayheight/100 ) {
                        // Show Actionbar
                        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                        MainActivity.getInstance().showSystemUI();
                        webView.invalidate();
                        return false;
                    }

                } catch (Exception e) {
                    webView.invalidate();
                }
                return false;
            }
        }
    }



}


