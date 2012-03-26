package com.tutorial.morningcookie;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tutorial.morningcookie.FeedItemNews.ArticleInfo;

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
                                Toast.makeText(mContext, "[ONCOMPLETE] " + feedItemNews.newsTitle(), Toast.LENGTH_LONG).show();
                                Log.i(TAG, feedItemNews.toString());
                                
                                TextToSpeechEngine ttsEngine = ((MorningCookieActivity)(mContext)).textToSpeechEngine(); 
                                
                                for (int index = 0; index < feedItemNews.articleInfo().size(); index++) {
                                    List<ArticleInfo> articleInfo = feedItemNews.articleInfo();
                                    ttsEngine.addItem(ttsEngine.new SpeechItem("news", 
                                                                               articleInfo.get(index).mTitle, 
                                                                               articleInfo.get(index).mLink));
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
    
    public void requestWeather(String urls) {
        Toast.makeText(mContext, "[requestWeather] " + urls, Toast.LENGTH_LONG).show();
        
        for(String url : urls.split(",")) {
            HttpRequester httpRequest = new HttpRequester(url, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                    case HttpRequester.MSG_ON_HTTP_REQUEST_COMPLETE:
                        try {
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            Document doc = builder.parse(new InputSource(new StringReader((String)msg.obj)));
                            
                            Element root = doc.getDocumentElement();
                            Toast.makeText(mContext, "[MSG_ON_HTTP_REQUEST_COMPLETE] " + (String)msg.obj, Toast.LENGTH_LONG).show();
                            
                            
                        } catch (Exception e) {
                            Toast.makeText(mContext, "[Handler Exception] " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                }
            });
            
            httpRequest.start();
        }
    }
    
    public void requestRSS(String urls) {
        Toast.makeText(mContext, "[requestRSS] " + urls, Toast.LENGTH_LONG).show();
        
        for(String url : urls.split(",")) {
            HttpRequester httpRequest = new HttpRequester(url, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                    case HttpRequester.MSG_ON_HTTP_REQUEST_COMPLETE:
                        try {
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            Document doc = builder.parse(new InputSource(new StringReader((String)msg.obj)));
                            
                            Element root = doc.getDocumentElement();
                            Toast.makeText(mContext, "[MSG_ON_HTTP_REQUEST_COMPLETE] " + (String)msg.obj, Toast.LENGTH_LONG).show();
                            
                            
                        } catch (Exception e) {
                            Toast.makeText(mContext, "[Handler Exception] " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
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
