package com.SuperNamek.NextVoz;

import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.widget.Toast;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @Author: SuperNamek
 */

public class Downloader implements DownloadListener {

    private final Activity activity;

    public Downloader(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Toast.makeText(activity.getBaseContext(), activity.getString(R.string.file_download_started), Toast.LENGTH_SHORT).show();

        final String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimetype);

        final String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);

        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription(activity.getString(R.string.file_download_started));
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Toast.makeText(activity.getBaseContext(), activity.getString(R.string.file_download_message), Toast.LENGTH_LONG).show();
    }
}