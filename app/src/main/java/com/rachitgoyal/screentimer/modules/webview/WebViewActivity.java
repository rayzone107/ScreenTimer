package com.rachitgoyal.screentimer.modules.webview;

import android.os.Bundle;
import android.view.MenuItem;

import com.asksira.webviewsuite.WebViewSuite;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.modules.base.BaseActivity;
import com.rachitgoyal.screentimer.util.Constants;

public class WebViewActivity extends BaseActivity {

    public static final int FAQs = 0;
    public static final int TERMS_AND_CONDITIONS = 1;
    public static final int PRIVACY_POLICY = 2;

    WebViewSuite mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = findViewById(R.id.webView);

        if (getIntent().hasExtra(Constants.EXTRAS.TYPE_OF_WEBVIEW)) {
            int type = getIntent().getIntExtra(Constants.EXTRAS.TYPE_OF_WEBVIEW, 0);

            switch (type) {
                case FAQs:
                    loadURL("http://rachitgoyal.com/faqs.html");
                    setTitle("FAQs");
                    break;
                case TERMS_AND_CONDITIONS:
                    loadURL("http://rachitgoyal.com/terms_and_conditions.html");
                    setTitle("Terms and Conditions");
                    break;
                case PRIVACY_POLICY:
                    loadURL("http://rachitgoyal.com/privacy_policy.html");
                    setTitle("Privacy Policy");
                    break;
            }
        }
    }

    private void loadURL(String url) {
        mWebView.startLoading(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
