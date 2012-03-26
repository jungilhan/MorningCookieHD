package com.tutorial.morningcookie;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import android.util.Log;

public abstract class FeedParser {
    private final String TAG = "FeedParser";
    private final String mLowData;
    protected FeedItem mFeedItem;
    private OnCompleteListener mOnCompleteListener;
    
    protected abstract void onStart(Element root);
    
    FeedParser(String lowData, FeedItem feedItem) {
        mFeedItem = feedItem;
        mLowData = lowData;
    }
    
    public void start() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(mLowData)));
            
            Log.d("TEST", mLowData);
            Element root = document.getDocumentElement();
            Element e = (Element)root.getElementsByTagName("title").item(0);
            Log.d("TEST", getElementValue(e));
            
            onStart(document.getDocumentElement());
            
        } catch (Exception e) {
            Log.e(TAG, "[FeedParser Exception] " + e.getMessage());
        }
        
        if (mOnCompleteListener != null)
            mOnCompleteListener.onComplete(mFeedItem);
    }
    
    protected String getElementValue(Element e) {
        if (e == null)
            return "";

        StringBuilder stringBuilder = new StringBuilder();
          
        Node node = e.getFirstChild();
        while (node != null) {
            stringBuilder.append(node.getNodeValue());
            node = node.getNextSibling();
        }
        
        return stringBuilder.toString();
    }
    
    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        mOnCompleteListener = onCompleteListener;
    }
    
    public interface OnCompleteListener {
        public void onComplete(FeedItem feedItem);
    }
}
