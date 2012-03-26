package com.tutorial.morningcookie;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpRequester extends Thread {
    private final String mUrl;
    private String mBuffer;
    private final Handler mHandler;
    public static final int MSG_ON_HTTP_REQUEST_COMPLETE = 0;
    
    HttpRequester(String url, Handler handler) {
        mUrl = url;
        mHandler = handler;
    }
    
    @Override
    public void run() {
        StringBuilder html = new StringBuilder();
        try {
            URL url = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            if (connection != null) {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    
                    for (;;) {
                        String line = bufferedReader.readLine();
                        if (line == null) break;
                        html.append(line);
                    }
                    bufferedReader.close();
                    mBuffer = html.toString();
                }
                connection.disconnect();
            }
        } catch (Exception e) {
            Log.e("HTTPRequester", "[HTTPRequester Exception] " + e.getMessage());
        }
        
        mHandler.sendMessage(Message.obtain(mHandler, 
                MSG_ON_HTTP_REQUEST_COMPLETE, 
                mBuffer));
    }
}
