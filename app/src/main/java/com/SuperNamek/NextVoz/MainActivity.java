package com.SuperNamek.NextVoz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.option:
                if(browserFragment.getAdblock_status() == true)
                {
                    browserFragment.setAdblock_status(false);
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable._adblock_off));
                }
                else
                    {
                        browserFragment.setAdblock_status(true);
                        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable._adblock_on));
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
}