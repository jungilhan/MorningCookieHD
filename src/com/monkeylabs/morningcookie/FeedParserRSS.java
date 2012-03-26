package com.monkeylabs.morningcookie;

import java.util.List;

import org.w3c.dom.Element;

import com.monkeylabs.morningcookie.FeedItemNews.ArticleInfo;

public class FeedParserRSS extends FeedParserNews {
    FeedParserRSS(String lowData, FeedItem feedItem) {
        super(lowData, feedItem);
    }
    
    @Override
    protected void onStart(Element root) {
        ((FeedItemNews)mFeedItem).putProvider(provider(root));
        ((FeedItemNews)mFeedItem).putArticleInfo(articleInfo(root));
    }
    
    @Override
    protected String provider(Element root) {
        return super.provider(root);
    }
    
    @Override
    protected List<ArticleInfo> articleInfo(Element root) {
        return super.articleInfo(root);
    }
}
