package com.monkeylabs.morningcookie;

import org.w3c.dom.Element;

public class FeedParserWeather extends FeedParser {
    FeedParserWeather(String lowData, FeedItem feedItem) {
        super(lowData, feedItem);
    }
    
    @Override
    protected void onStart(Element root) {
        ((FeedItemWeather)mFeedItem).putProvider(provider(root));
        ((FeedItemWeather)mFeedItem).putForecast(forecast(root));
        ((FeedItemWeather)mFeedItem).putLink(link(root));
    }

    private String provider(Element root) {
        Element e = (Element)root.getElementsByTagName("title").item(0);
        return getElementValue(e);
    }
    
    private String forecast(Element root) {
        Element e = (Element)root.getElementsByTagName("wf").item(0);
        return getElementValue(e).replaceAll("<br.*?>", "");
    }
    
    private String link(Element root) {
        return "http://www.kma.go.kr/weather/main.jsp";
    }
}
