package com.monkeylabs.morningcookie;

import java.util.List;

public class FeedItemNews implements FeedItem {
    private String mProvider;
    private List<ArticleInfo> mArticleInfo;
    
    @Override
    public String toString() {
        String text = mProvider + " ";
        for (int index = 0; index < mArticleInfo.size(); index++)
            text += mArticleInfo.get(index).mTitle + " ";
        
        return text;
    }
    
    public String provider() {
        return mProvider;
    }
    
    public void putProvider(String provider) {
        mProvider = provider;
    }
    
    public List<ArticleInfo> articleInfo() {
        return mArticleInfo;
    }
    
    public void putArticleInfo(List<ArticleInfo> articleInfo) {
        mArticleInfo = articleInfo;
    }
    
    public class ArticleInfo {
        public String mTitle;
        public String mLink;
        
        ArticleInfo(String title, String link) {
            mTitle = title;
            mLink = link;
        }
    }
}