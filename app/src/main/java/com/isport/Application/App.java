package com.isport.Application;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Euphoria on 2017/7/27.
 */
public class App extends Application {
    private static App sInstance;
    public static RequestQueue queue;
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        sInstance = this;
        Fresco.initialize(this);
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    public static App getInstance() { return sInstance; }

    public static RequestQueue getHttpQueue() { return queue; }

}