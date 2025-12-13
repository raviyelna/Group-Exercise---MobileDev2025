package com.example.comicreaderapp.manga_model;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

public class NetworkSingleton {
    private static NetworkSingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private NetworkSingleton(Context context) {
        ctx = context.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx);
        }
        return requestQueue;
    }
}