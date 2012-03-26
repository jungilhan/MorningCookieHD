package com.monkeylabs.morningcookie;

public class FeedItemWeather implements FeedItem {
    private String mProvider;
    private String mForecast;
    private String mLink;
    
    public String provider() {
        return mProvider;
    }
    
    public void putProvider(String provider) {
        mProvider = provider;
    }
    
    public String Forecast() {
        return mForecast;
    }
    
    public void putForecast(String forecast) {
        mForecast = forecast;
    }
    
    public String link() {
        return mLink;
    }
    
    public void putLink(String link) {
        mLink = link;
    }
    
    @Override
    public String toString() {
        String text = "기상청에서 제공하는 " + mProvider + "입니다. " + mForecast;
        return text;
    }
}
