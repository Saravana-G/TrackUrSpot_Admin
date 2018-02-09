package com.tusadmin.trackurspot_admin;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.tusadmin.trackurspot_admin.Extras.FontsOverride;
import com.tusadmin.trackurspot_admin.Extras.QuickstartPreferences;


public class StartUpActivity extends Activity implements Animation.AnimationListener {
    ImageView imageView;
    Animation animation2;
    Boolean exis = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_up);
        QuickstartPreferences.initQuickstartPreferences(getApplicationContext());

        //To clear all the notification once the user enters the app
        QuickstartPreferences.modifyString(QuickstartPreferences.NOTIFICATION_MESSAGE,"");

        if(!(QuickstartPreferences.extractString(QuickstartPreferences.USER_EMAIL).equals(""))){
            exis=true;
        }

       // FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/segoepr.ttf");
        imageView = (ImageView) findViewById(R.id.gifImage);

       // String fontPath = "fonts/segoepr.ttf";
       // Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        //SETTING A ANIMATION FOR LOAD PAGE
        animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.quake);
        imageView.startAnimation(animation2);
        animation2.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animation2) {
            if (!exis) {
                Intent i = new Intent(StartUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            } else {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

}