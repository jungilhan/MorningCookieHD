package com.tutorial.morningcookie;

import org.apache.cordova.DroidGap;

import android.webkit.WebView;
import android.widget.Toast;

public class NativeAdapter {
    private final DroidGap mDroidGap;
    private final WebView mWebView;
    private final FeedManager mFeedManager;
    
    public NativeAdapter(DroidGap droidGap, WebView webView) {
        mDroidGap = droidGap;
        mWebView = webView;
        mFeedManager = new FeedManager(mDroidGap.getContext());
    }
    
    public void handleMessage(String message, String arg1, String arg2) {
        Toast.makeText(mDroidGap, "[handleMessage] " + message + " " + arg1 + " " + arg2, Toast.LENGTH_SHORT).show();
        
        if (message.equals("feedRequest")) {
            if (arg1.equals("news")) {
                mFeedManager.requestNews(arg2, new FeedManager.OnRequestCompleteListener() {
                    public void onRequestComplete(String stream) {
                        sendMessage("onFeedRequestComplete", "news", stream);
                    }
                });
                
            } else if (arg1.equals("weather")) {
                mFeedManager.requestWeather(arg2);
                
            } else if (arg1.equals("rss")) {
                mFeedManager.requestRSS(arg2);
                
            } else if (arg1.equals("facebook")) {
                mFeedManager.requestFacebook(arg2);
                
            } else if (arg1.equals("twitter")) {
                mFeedManager.requestTwitter(arg2);
                
            } else if (arg1.equals("stock")) {
                mFeedManager.requestStock(arg2);
                
            }
        } else if (message.equals("ttsRequest")) {
            if (arg1.equals("true")) {
                ((MorningCookieActivity)mDroidGap).textToSpeechEngine().speak();
                
            } else if (arg1.equals("false")) {
                ((MorningCookieActivity)mDroidGap).textToSpeechEngine().stop();
            }
        }
    }
    
    public void sendMessage(String message, String arg1, String arg2) {
        if (message.equals("onFeedRequestComplete")) {
            arg2 = arg2.replace("\"", "\\" + "\"");
            arg2 = arg2.replace("\'", "\\" + "\'");
        }
        
        String js = "javascript:(function() { handleMessage(" + "\"" + message + "\"," + "\"" + arg1 + "\"," + "\"" + arg2 + "\"" + ");})()";
        //Toast.makeText(mDroidGap, "[sendMessage] " + js, Toast.LENGTH_LONG).show();
        mWebView.loadUrl(js);
    }
    
    public boolean fooBoolean() {
        return false;
    }
}
