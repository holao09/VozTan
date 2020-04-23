package com.SuperNamek.NextVoz.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.SuperNamek.NextVoz.CustomizedWebViewClient;
import com.SuperNamek.NextVoz.Downloader;
import com.SuperNamek.NextVoz.R;
import com.SuperNamek.NextVoz.adblock.AdBlockWebViewClient;
import com.SuperNamek.NextVoz.events.EventRedirectBrowser;

/**
 * @Author: SuperNamek
 */

public class BrowserFragment extends Fragment {

    public static final String TAG = "WEBVIEW_FRAGMENT";

    private WebView webView;
    private WebSettings webSettings;

    private AdBlockWebViewClient adBlockWebViewClient = new AdBlockWebViewClient(true);

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

    @Subscribe(sticky = true)
    public void onRedirectBrowser(EventRedirectBrowser event) {
        webView.loadUrl(event.getUrl());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);

        webView = (WebView)view.findViewById(R.id.webview);
        //webView.setAdblockEnabled(true);
        //adblock_status = true;

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

    public WebView getWebView() {
        return this.webView;
    }

    private void initWebView() {
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.clearCache(true);
        webView.setWebChromeClient(new WebChromeClient());
        //webView.setWebViewClient(new CustomizedWebViewClient());
        webView.setWebViewClient(adBlockWebViewClient);
        webView.setDownloadListener(new Downloader(getActivity()));
    }

}
