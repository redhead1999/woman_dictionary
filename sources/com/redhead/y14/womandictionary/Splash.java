package com.redhead.y14.womandictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.p003v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    /* renamed from: iv */
    private ImageView f39iv;

    /* renamed from: tv */
    private TextView f40tv;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) C0351R.layout.activity_splash);
        this.f39iv = (ImageView) findViewById(C0351R.C0353id.f35iv);
        Animation loadAnimation = AnimationUtils.loadAnimation(this, C0351R.anim.transition);
        this.f40tv = (TextView) findViewById(C0351R.C0353id.f36tv);
        Animation loadAnimation2 = AnimationUtils.loadAnimation(this, C0351R.anim.transition);
        this.f39iv.startAnimation(loadAnimation);
        this.f40tv.startAnimation(loadAnimation2);
        final Intent intent = new Intent(this, DictionaryMainActivity.class);
        new Thread() {
            public void run() {
                try {
                    sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Throwable th) {
                    Splash.this.startActivity(intent);
                    Splash.this.finish();
                    throw th;
                }
                Splash.this.startActivity(intent);
                Splash.this.finish();
            }
        }.start();
    }
}
