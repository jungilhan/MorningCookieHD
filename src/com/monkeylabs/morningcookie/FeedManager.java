package com.monkeylabs.morningcookie;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.monkeylabs.morningcookie.FeedItemNews.ArticleInfo;

public class FeedManager {
    private final String TAG = "FeedManager";
    private HttpRequester mHttpRequester;
    private final Context mContext;
    
    FeedManager(Context context) {
        mContext = context;
    }
    
    public void requestNews(String urls, final OnRequestCompleteListener onRequestCompleteListener) {
        //Toast.makeText(mContext, "[requestNews] " + urls, Toast.LENGTH_LONG).show();
        
        for(String url : urls.split(",")) {
            HttpRequester httpRequest = new HttpRequester(url, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                    case HttpRequester.MSG_ON_HTTP_REQUEST_COMPLETE:
                        FeedParser parser = new FeedParserNews((String)msg.obj, new FeedItemNews());
                        parser.setOnCompleteListener(new FeedParser.OnCompleteListener() {
                            public void onComplete(FeedItem feedItem) {
                                FeedItemNews feedItemNews = (FeedItemNews)feedItem;
                                Toast.makeText(mContext, "[ONCOMPLETE] " + feedItemNews.provider(), Toast.LENGTH_SHORT).show();
                                //Log.i(TAG, feedItemNews.toString());
                                
                                TextToSpeechEngine ttsEngine = ((MorningCookieActivity)(mContext)).textToSpeechEngine(); 
                                
                                for (int index = 0; index < feedItemNews.articleInfo().size(); index++) {
                                    List<ArticleInfo> articleInfo = feedItemNews.articleInfo();
                                    ttsEngine.addItem(ttsEngine.new SpeechItem("news", feedItemNews.provider(),
                                                                               articleInfo.get(index).mTitle, 
                                                                               articleInfo.get(index).mLink,
                                                                               index == 0 ? true : false));
                                }
                                
                                if (onRequestCompleteListener != null)
                                    onRequestCompleteListener.onRequestComplete(feedItemNews.toString());
                            }
                        });
                        
                        parser.start();
                        break;
                    }
                }
            });
            
            httpRequest.start();
        }
    }
    
    public void requestWeather(String urls, final OnRequestCompleteListener onRequestCompleteListener) {
        Toast.makeText(mContext, "[requestWeather] " + urls, Toast.LENGTH_LONG).show();
        
        for(String url : urls.split(",")) {
            HttpRequester httpRequest = new HttpRequester(url, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                    case HttpRequester.MSG_ON_HTTP_REQUEST_COMPLETE:
                        FeedParser parser = new FeedParserWeather((String)msg.obj, new FeedItemWeather());
                        parser.setOnCompleteListener(new FeedParser.OnCompleteListener() {
                            public void onComplete(FeedItem feedItem) {
                                FeedItemWeather feedItemWeather = (FeedItemWeather)feedItem;
                                Toast.makeText(mContext, "[ONCOMPLETE] " + feedItemWeather.provider(), Toast.LENGTH_SHORT).show();
                                //Log.i(TAG, feedItemWeather.toString());
                                
                                TextToSpeechEngine ttsEngine = ((MorningCookieActivity)(mContext)).textToSpeechEngine();                                
                                ttsEngine.addItem(ttsEngine.new SpeechItem("weather", feedItemWeather.provider(),
                                        feedItemWeather.Forecast(), 
                                        feedItemWeather.link(),
                                        true));
                                
                                if (onRequestCompleteListener != null)
                                    onRequestCompleteListener.onRequestComplete(feedItemWeather.toString());
                            }
                        });
                        
                        parser.start();
                        break;
                    }
                }
            });
            
            httpRequest.start();
        }
    }
    
    public void requestRSS(String urls, final OnRequestCompleteListener onRequestCompleteListener) {
        Toast.makeText(mContext, "[requestRSS] " + urls, Toast.LENGTH_LONG).show();
        
        for(String url : urls.split(",")) {
            HttpRequester httpRequest = new HttpRequester(url, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                    case HttpRequester.MSG_ON_HTTP_REQUEST_COMPLETE:
                        FeedParser parser = new FeedParserRSS((String)msg.obj, new FeedItemRSS());
                        parser.setOnCompleteListener(new FeedParser.OnCompleteListener() {
                            public void onComplete(FeedItem feedItem) {
                                FeedItemNews feedItemRSS = (FeedItemNews)feedItem;
                                Toast.makeText(mContext, "[ONCOMPLETE] " + feedItemRSS.provider(), Toast.LENGTH_SHORT).show();
                                //Log.i(TAG, feedItemRSS.toString());
                                
                                TextToSpeechEngine ttsEngine = ((MorningCookieActivity)(mContext)).textToSpeechEngine(); 
                                
                                for (int index = 0; index < feedItemRSS.articleInfo().size(); index++) {
                                    List<ArticleInfo> articleInfo = feedItemRSS.articleInfo();
                                    ttsEngine.addItem(ttsEngine.new SpeechItem("news", feedItemRSS.provider(),
                                                                               articleInfo.get(index).mTitle, 
                                                                               articleInfo.get(index).mLink,
                                                                               index == 0 ? true : false));
                                }
                                
                                if (onRequestCompleteListener != null)
                                    onRequestCompleteListener.onRequestComplete(feedItemRSS.toString());
                            }
                        });

                        parser.start();
                        break;
                    }
                }
            });
            
            httpRequest.start();
        }
    }
    
    public void requestFacebook(String url) {
        Toast.makeText(mContext, "[requestFacebook] " + url, Toast.LENGTH_LONG).show();
    }
    
    public void requestTwitter(String url) {
        Toast.makeText(mContext, "[requestTwitter] " + url, Toast.LENGTH_LONG).show();
    }
    
    public void requestStock(String url) {
        Toast.makeText(mContext, "[requestStock] " + url, Toast.LENGTH_LONG).show();
    }
    
    public interface OnRequestCompleteListener {
        public void onRequestComplete(String stream);
    }
}
