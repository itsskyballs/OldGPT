package com.pususheen.lowpolychatgpt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    private static final String TAG = "OldGPT";
    private static final String CHAT_URL = "https://chat.openai.com";

    private WebView webView;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.web_container);
        Button exitButton = findViewById(R.id.exit_button);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitDialog();
            }
        });

        boolean geckoAvailable = isClassPresent("org.mozilla.geckoview.GeckoView");
        if (geckoAvailable) {
            Log.i(TAG, "GeckoView classes found at runtime. If you added GeckoView AAR and native libs, you can initialize Gecko here.");
            // Developer: implement GeckoLoader.initWithGecko(...) after adding geckoview.aar and native libs
            try {
                com.pususheen.lowpolychatgpt.GeckoLoader.initWithGecko(this, container);
            } catch (Throwable t) {
                Log.w(TAG, "Gecko initialization failed: " + t.getMessage());
                setupWebViewFallback();
            }
        } else {
            Log.i(TAG, "GeckoView not available - falling back to WebView");
            setupWebViewFallback();
        }
    }

    private void setupWebViewFallback() {
        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // Use an older-compatible user agent string but still modern enough
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.91 Mobile Safari/537.36");

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.w(TAG, "WebView error: " + errorCode + " " + description);
                view.loadUrl("file:///android_asset/offline.html");
            }
        });

        container.addView(webView);
        try {
            webView.loadUrl(CHAT_URL);
        } catch (Exception e) {
            Log.e(TAG, "Failed to load URL in WebView", e);
            webView.loadUrl("file:///android_asset/offline.html");
        }
    }

    private boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit Application?");
        builder.setMessage("You can chat later.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                showExitDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
