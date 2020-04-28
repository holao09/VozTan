package com.SuperNamek.NextVoz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.SuperNamek.NextVoz.adblock.AdBlocker;
import com.SuperNamek.NextVoz.events.EventDisplayError;
import com.SuperNamek.NextVoz.events.EventRedirectBrowser;
import com.SuperNamek.NextVoz.events.EventUseBrowserFragment;
import com.SuperNamek.NextVoz.events.EventWebError;
import com.SuperNamek.NextVoz.fragments.BrowserFragment;
import com.SuperNamek.NextVoz.fragments.ErrorFragment;

import javax.net.ssl.SSLSession;

import rx.schedulers.Schedulers;

/**
 * @Author: SuperNamek
 *
 * https://developer.android.com/guide/webapps/webview.html
 */

public class MainActivity extends AppCompatActivity {

    private BrowserFragment browserFragment;
    private ErrorFragment errorFragment;
    Menu menu;
    private static MainActivity instance;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onUseBrowserFragment(EventUseBrowserFragment event) {
        createBrowserFragment();
    }

    @Subscribe
    public void onWebErrorOccurred(EventWebError eventWebError) {
        errorFragment = new ErrorFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, errorFragment, ErrorFragment.TAG)
                .commit();
        getSupportFragmentManager().executePendingTransactions();

        EventBus.getDefault().postSticky(new EventDisplayError(eventWebError.getErrorDescription()));

        Toast.makeText(MainActivity.this, getString(R.string.error) + " " + eventWebError.getErrorDescription(), Toast.LENGTH_SHORT).show();

        browserFragment = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createBrowserFragment();
        AdBlocker.init(this, Schedulers.io());
        instance = this;
    }

    @Override
    public void onBackPressed() {
        if (browserFragment != null && browserFragment.getWebView() != null) {
            WebView webView = browserFragment.getWebView();
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("ShowToast")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.option:
                if(browserFragment.getAdblock_status() == true)
                {
                    browserFragment.setAdblock_status(false);
                    menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.adblock_off));
                    Toast.makeText(MainActivity.this,R.string.ads_on_msg,Toast.LENGTH_SHORT).show();
                    browserFragment.getWebView().reload();
                }
                else
                    {
                        browserFragment.setAdblock_status(true);
                        menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.adblock_on));
                        Toast.makeText(MainActivity.this,R.string.ads_off_msg,Toast.LENGTH_SHORT).show();
                        browserFragment.getWebView().reload();
                    }
                return true;

            case R.id.f17:
                EventBus.getDefault().postSticky(new EventRedirectBrowser(getString(R.string.f17url)));
                return true;
            case R.id.f33:
                EventBus.getDefault().postSticky(new EventRedirectBrowser(getString(R.string.f33url)));
                return true;

            case R.id.menu_item_product_info:
                EventBus.getDefault().postSticky(new EventRedirectBrowser(getString(R.string.URL_INFO)));
                return true;
            case R.id.menu_item_submit_bug_report:
                String[] addresses = new String[1];
                addresses[0] = getString(R.string.BUG_REPORT_MAIL_ADDRESS);
                composeEmail(addresses, getString(R.string.BUG_REPORT_MAIL_SUBJECT));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createBrowserFragment() {
        browserFragment = new BrowserFragment();

        getSupportFragmentManager()
                .beginTransaction()
                //.add(R.id.frame_container, browserFragment, BrowserFragment.TAG)
                .add(R.id.frame_container,browserFragment,BrowserFragment.TAG)
                .commit();
        getSupportFragmentManager().executePendingTransactions();

        errorFragment = null;
    }

    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
        else
        {
            showSystemUI();
        }
    }
    public void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    public void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static MainActivity getInstance()
    {
        return instance;
    }
}