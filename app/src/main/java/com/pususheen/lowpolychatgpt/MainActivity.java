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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = "OldGPT.MainActivity";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);
        TextView startupMessage = new TextView(this);
        startupMessage.setText("OldGPT for old phones\n\nThis embedded app allows you to chat on an old Android phone.\n\nIf you have any issues with this embedded app, feel free to file an issue.");
        RelativeLayout.LayoutParams messageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        messageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        messageParams.setMargins(16,16,16,0);
        layout.addView(startupMessage, messageParams);

        // Container for content
        RelativeLayout content = new RelativeLayout(this);
        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        contentParams.addRule(RelativeLayout.BELOW, startupMessage.getId());

        // Attempt to use GeckoView if available (scaffolded). If not, fall back to WebView.
        boolean geckoAvailable = false;
        try {
            Class.forName("org.mozilla.geckoview.GeckoView");
            geckoAvailable = true;
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "GeckoView not found on classpath, falling back to WebView");
        }

        if (geckoAvailable) {
            // Developer note: real Gecko initialization is required with proper AAR and native libs.
            // The scaffold below will simply show a notice until GeckoLoader is wired with real binaries.
            TextView geckoNotice = new TextView(this);
            geckoNotice.setText("Gecko libraries detected. If you have added GeckoView AAR and native libs, you can enable Gecko rendering by completing GeckoLoader implementation.");
            RelativeLayout.LayoutParams gnParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            gnParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            content.addView(geckoNotice, gnParams);

            // Try to initialize via helper (no-op until you add geckoview.aar and native libraries)
            try {
                GeckoLoader.initWithGecko(this, content);
            } catch (Throwable t) {
                Log.w(TAG, "GeckoLoader failed to initialize: " + t.getMessage());
            }
        } else {
            webView = new WebView(this);
            RelativeLayout.LayoutParams webParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            content.addView(webView, webParams);

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setDatabaseEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            // Old Android WebView user agent improvements
            webSettings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.4; en-us) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0 Mobile Safari/537.36");

            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    Log.w(TAG, "WebView error: " + description + " (" + errorCode + ")");
                    view.loadUrl("file:///android_asset/offline.html");
                }
            });

            webView.loadUrl("https://chat.openai.com");
        }

        RelativeLayout.LayoutParams contentWrapParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        contentWrapParams.addRule(RelativeLayout.BELOW, startupMessage.getId());
        layout.addView(content, contentWrapParams);

        Button exitButton = new Button(this);
        exitButton.setText("Exit");
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitDialog();
            }
        });

        RelativeLayout.LayoutParams exitParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        exitParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        exitParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        exitParams.setMargins(0, 16, 16, 0);

        layout.addView(exitButton, exitParams);

        setContentView(layout);
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
