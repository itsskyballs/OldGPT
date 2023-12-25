package com.Pususheen.lowpolychatgpt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import android.app.*;
import java.net.*;

public class MainActivity extends Activity {
    private WebView webView;
    private boolean isFirstLoad = true;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = new MediaPlayer();


        RelativeLayout layout = new RelativeLayout(this);

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
		webSettings.getDefaultFixedFontSize();
		webSettings.setUserAgentString("Mozilla/5.0 (Linux: Android 10.0 ; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description, failingUrl);
					showErrorDialog();
				}
			});

        webView.loadUrl("https://chat.openai.com");
        setContentView(layout);
    }


    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		setTitle("Error - OldGPT");
        builder.setTitle("Error Loading Page");
        builder.setMessage("Connection refused or timed out. Check your internet connection.");
        builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int notify) {
					webView.reload();
					showToast("Attempting to refresh");
					setTitle("OldGPT");
				}
			});
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					finish();
				}
			});
        builder.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit Application?");
        builder.setMessage("This will quit OldGPT and the ChatGPT website. \nAre you sure you want to do it?");
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
		builder.setNeutralButton("Background", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					onBackPressed();
					showToast("OldGPT is running in headless state");
				}
			});
        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (webView.canGoBack()) {
                webView.goBack();
				setTitle("OldGPT");
                return true;
            } else {
                if (isFirstLoad) {
                    isFirstLoad = false;
					setTitle("OldGPT");
                } else {
                    showExitDialog();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_snapshot:
                saveSnapshot();
                return true;
            case R.id.menu_open_snapshot:
                openSnapshot();
                return true;
            case R.id.menu_exit:
                showExitDialog();
                return true;
			case R.id.menu_refresh:
			    webView.reload();
				return true;
			case R.id.menu_uastest:
				setTitle("UA String test - OldGPT");
			    webView.loadUrl("https://www.whatismybrowser.com");
				showToast("Redirecting to WhatIsMyBrowser.com");
				return true;
		    case R.id.menu_about:
				about();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About OldGPT");
        builder.setMessage("OldGPT 1.3 Gyatt\n\nOldGPT is an free amd open-source software(under licensed with GNU GPL 2.0)allows you to access ChatGPT for older devices\nIt uses User agent string to fake a browser/application that way you can access ChatGPT on your older devices\n\nLead Developer:\nSteven Kieth(Pususheen/stevenkiethanete)\n\nGYATTTTTT😳😳😳🔥🗣️🗣️🗣️");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {

				}
			});
        builder.setNegativeButton("GitHub", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					webView.loadUrl("github.com/stevenkiethanete/OldGPT");
					setTitle("Official GitHub Repo - OldGPT");
				}
			});
		builder.setNeutralButton("itch.io", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					webView.loadUrl("stevenkiethanete.itch.io/OldGPT");
					setTitle("stevenkiethanete on itch.io - OldGPT");

				}
			});
        builder.show();
    }
    private void saveSnapshot() {
        webView.saveWebArchive(getSnapshotFilename());
    }

	private String getSnapshotFilename() {
        String fileName = "Snapshot-" + generateRandomString(10) + ".odgpt";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }

        return randomString.toString();
    }

    private void openSnapshot() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        intent.setDataAndType(uri, "*/*");
        startActivity(Intent.createChooser(intent, "Open Snapshot"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
	

