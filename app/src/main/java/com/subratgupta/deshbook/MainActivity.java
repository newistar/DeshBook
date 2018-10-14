package com.subratgupta.deshbook;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    String MODE = "mode";
    final int BASIC = 0;
    final int MOBILE = 1;
    final int DESKTOP = 2;
    WebView myWebView;
    final String PLAY_MODE = "play_mode";
    SharedPreferences.Editor editor;
    SwipeRefreshLayout mySwipeRefreshLayout;
    String newUA = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
    private String url_radio1 = "http://212.83.138.48:8740";
    private String url_radio2 = "http://64.71.79.181:5124";
    ImageButton play_button;
    SharedPreferences sharedPref;
    private MediaPlayer player;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.webview);
        mySwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeContainer);
        play_button = (ImageButton) findViewById(R.id.play_btn);

        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putBoolean(PLAY_MODE, false);
        editor.commit();

        webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);

        if (sharedPref.getBoolean("enable", true)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRating();
                    editor.putBoolean("enable", false);
                    editor.commit();
                }
            }, 10 * 60 * 1000);
            editor.commit();
        }

        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                myWebView.setVisibility(View.GONE);
                findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myWebView.setVisibility(View.VISIBLE);
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    }
                }, 1000);

                if (url.contains("browser_fallback_url=https://m.facebook.com/messages/")) {
                    view.loadUrl("https://m.facebook.com/messages?soft=messages");
                } else {
                    view.loadUrl(url);
                }
                return false;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //Clearing the WebView
                try {
                    myWebView.stopLoading();
                } catch (Exception e) {
                }
                try {
                    myWebView.clearView();
                } catch (Exception e) {
                }
                if (myWebView.canGoBack()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myWebView.goBack();
                        }
                    }, 1);
                }
                super.onReceivedError(myWebView, errorCode, description, failingUrl);
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        myWebView.reload();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        }, 1000);
                    }
                }
        );

        loadPage();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (myWebView.canGoBack()) {
                        myWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        editor.putBoolean(PLAY_MODE, false);
        editor.commit();
        super.onDestroy();
    }

    private void showRating() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.webview).setVisibility(View.GONE);
                findViewById(R.id.rating_layout).setVisibility(View.VISIBLE);
                addListenerOnRatingBar();
            }
        }, 5 * 1000);

    }

    public void play_pause(View view) {
        if (sharedPref.getBoolean(PLAY_MODE, false)) {
            stopPlaying();
        } else {
            startPlaying();
        }
    }

    private void startPlaying() {
        initializeMediaPlayer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        player.start();
                    }
                });
                editor.putBoolean(PLAY_MODE, true);
                editor.commit();
                play_button.setImageResource(R.drawable.ic_pause_black_24dp);
            }
        }, 1 * 500);


    }

    private void stopPlaying() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
            editor.putBoolean(PLAY_MODE, false);
            editor.commit();
            play_button.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource(url_radio2);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.i("Buffering", "" + percent);
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.basic:
                if (checked)
                    editor.putInt(MODE, BASIC);
                break;
            case R.id.mobile:
                if (checked)
                    editor.putInt(MODE, MOBILE);
                break;
            case R.id.desktop:
                if (checked)
                    editor.putInt(MODE, DESKTOP);
                break;
        }
        editor.commit();
        loadPage();
    }

    private void loadPage() {
        switch (sharedPref.getInt(MODE, 5)) {
            case BASIC:
                myWebView.loadUrl("https://mbasic.facebook.com");
                ((RadioButton) findViewById(R.id.basic)).setChecked(true);
                break;
            case MOBILE:
                myWebView.loadUrl("https://m.facebook.com");
                ((RadioButton) findViewById(R.id.mobile)).setChecked(true);
                break;
            case DESKTOP:
                myWebView.getSettings().setUserAgentString(newUA);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(true);
                myWebView.loadUrl("https://www.facebook.com");
                ((RadioButton) findViewById(R.id.desktop)).setChecked(true);
                break;
            default:
                myWebView.loadUrl("https://fb.com");
                ((RadioButton) findViewById(R.id.mobile)).setChecked(true);
        }
    }

    public void addListenerOnRatingBar() {

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                findViewById(R.id.webview).setVisibility(View.VISIBLE);
                findViewById(R.id.rating_layout).setVisibility(View.GONE);
                Float rate = Float.valueOf(String.valueOf(rating));
                if (rate >= 4) {
                    Toast.makeText(getApplicationContext(), "Please rate us on Google PlayStore.", Toast.LENGTH_LONG).show();

                    Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "ThankYou!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
