package com.SuperNamek.NextVoz;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.greenrobot.eventbus.EventBus;

import com.SuperNamek.NextVoz.events.EventWebError;

/**
 * @Author: SuperNamek
 */

public class CustomizedWebViewClient extends WebViewClient {

    private boolean receivedError;

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        receivedError = false;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        receivedError = true;
        view.setVisibility(View.GONE);
        EventBus.getDefault().post(new EventWebError(description));
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (!receivedError) {
            view.setVisibility(View.VISIBLE);
        }
        receivedError = false;
    }
}
