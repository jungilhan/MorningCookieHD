package com.monkeylabs.morningcookie;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.monkeylabs.morningcookie.FeedItemNews.ArticleInfo;

public class FeedParserNews extends FeedParser {
    FeedParserNews(String lowData, FeedItem feedItem) {
        super(lowData, feedItem);
    }
    
    @Override
   protected void onStart(Element root) {
        ((FeedItemNews)mFeedItem).putNewsTitle(newsTitle(root));
        ((FeedItemNews)mFeedItem).putArticleInfo(articleInfo(root));
    }

    private String newsTitle(Element root) {
        Element e = (Element)root.getElementsByTagName("title").item(0);
        return getElementValue(e);
    }
    
    private List<ArticleInfo> articleInfo(Element root) {
        List<ArticleInfo> aticleInfo = new ArrayList<ArticleInfo>();
        NodeList item = root.getElementsByTagName("item");
        
        for (int index = 0; index < item.getLength(); index++) {
            Element title = (Element)((Element)item.item(index)).getElementsByTagName("title").item(0);
            Element link = (Element)((Element)item.item(index)).getElementsByTagName("link").item(0);
            
            ArticleInfo articleInfo = ((FeedItemNews)mFeedItem).new ArticleInfo(getElementValue(title), getElementValue(link));
            aticleInfo.add(articleInfo);
        }
        
        return aticleInfo;
    }
}
