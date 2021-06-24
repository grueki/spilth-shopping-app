package dev.jacksonc.spilth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Graphic screen on cold start of app.
 *
 * @author Izzy
 */
public class SplashActivity extends Activity {

    // Creating object for logo text
    TextView logo;

    // Create object for logo's animation
    Animation logo_anim;

    // Create object for splash screen handler
    Handler handler;

    /**
     * Initialises the splash screen and creates its components.
     *
     * @param savedInstanceState Activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Set for splash screen to show for 3 seconds
        handler=new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);

            // Splash screen fades in and out smoothly over the MainActivity
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        },3000);

        // Get logo and logo's animation, then play animation
        logo = findViewById(R.id.splash_logo);
        logo_anim = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        logo.startAnimation(logo_anim);

    }
}