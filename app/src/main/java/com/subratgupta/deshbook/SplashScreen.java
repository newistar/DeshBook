package com.subratgupta.deshbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.green).setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.white).setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.orange).setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.circle).setVisibility(View.VISIBLE);
                                        RotateAnimation rotateAnimation = new RotateAnimation(0, 30f,
                                                Animation.RELATIVE_TO_SELF, 0.5f,
                                                Animation.RELATIVE_TO_SELF, 0.5f);

                                        rotateAnimation.setInterpolator(new LinearInterpolator());
                                        rotateAnimation.setDuration(1000);
                                        rotateAnimation.setRepeatCount(Animation.INFINITE);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                findViewById(R.id.logo).setVisibility(View.VISIBLE);

                                            }
                                        }, 1 * 500);

                                        findViewById(R.id.circle).startAnimation(rotateAnimation);
                                    }
                                }, 1 * 500);
                            }
                        }, 1 * 500);
                    }
                }, 1 * 500);
            }
        }, 1 * 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
        }, 3 * 1000); // wait for 5 seconds
    }
}

