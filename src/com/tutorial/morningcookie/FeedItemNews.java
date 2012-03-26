package com.tutorial.morningcookie;

import java.util.List;

public class FeedItemNews implements FeedItem {
    private String mTitle;
    private List<ArticleInfo> mArticleInfo;
    
    public String newsTitle() {
        return mTitle;
    }
    
    @Override
    public String toString() {
        String news = mTitle + " ";
        for (int index = 0; index < mArticleInfo.size(); index++) {
            news += mArticleInfo.get(index).mTitle + " ";
        }
        
        return news;
    }
    
    public void putNewsTitle(String title) {
        mTitle = title;
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