package com.Pususheen.lowpolychatgpt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);
        TextView startupMessage = new TextView(this);
        startupMessage.setText("OldGPT for old phones\n\nThis embedded app allows you to chat on an old Android phone.\n\nIf you have any issues with this embedded app, feel free to file an issue on my official GitHub repo.");
        RelativeLayout.LayoutParams messageParams = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        messageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(startupMessage, messageParams);

        webView = new WebView(this);
        RelativeLayout.LayoutParams webParams = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.MATCH_PARENT,
			RelativeLayout.LayoutParams.MATCH_PARENT
        );
        layout.addView(webView, webParams);
		
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://chat.openai.com");

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
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                showExitDialog(); 
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
