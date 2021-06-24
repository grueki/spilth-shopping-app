package dev.jacksonc.spilth;

import android.app.Application;
import android.content.Context;


/**
 * Application class that provides global context
 *
 * @author Jackson
 */
public class Spilth extends Application {
    private static Spilth instance;

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
