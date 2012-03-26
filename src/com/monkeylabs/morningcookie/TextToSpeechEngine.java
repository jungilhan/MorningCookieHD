package com.monkeylabs.morningcookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

public class TextToSpeechEngine {
    private final String TAG = "TextToSpeechEngine";
    private final Context mContext;
    private final TextToSpeech mTextToSpeech;
    private final List<SpeechItem> mSpeechItems;
    private int mIndex;
    private boolean mIsExplicitStop = false;
    
    TextToSpeechEngine(Context context) {
        mContext = context;
        mSpeechItems = new ArrayList<SpeechItem>();
        
        mTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTextToSpeech.setLanguage(Locale.KOREAN);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(mContext, "TTS engine does not support the Korean", Toast.LENGTH_SHORT).show();
                        
                    } else {
                        mTextToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                            public void onUtteranceCompleted(String utteranceId) {
                                int index = Integer.parseInt(utteranceId);
                                SpeechItem item = mSpeechItems.get(index);
                                Log.d(TAG, "[onUtteranceCompleted] " + item.mCategory + " " + item.mTitle + " " + item.mLink);
                                
                                if (!mIsExplicitStop) {
                                    MorningCookieActivity mainActivity = (MorningCookieActivity)mContext;
                                    mainActivity.sendMessage(MorningCookieActivity.MSG_VOICE_RECOGNIZER_REQUEST, item);
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(mContext, "Could not initialize TextToSpeech", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    public void addItem(SpeechItem item) {
        mSpeechItems.add(item);
    }
    
    public int speak() {
        if (mSpeechItems.size() == 0)
            return -1;
        
        if (mIndex >= mSpeechItems.size())
            mIndex = 0;

        mIsExplicitStop = false;
        
        SpeechItem item = mSpeechItems.get(mIndex);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, Integer.toString(mIndex));
        mIndex++;
        
        return mTextToSpeech.speak(preprocessing(item), TextToSpeech.QUEUE_FLUSH, params);
    }
    
    private String preprocessing(SpeechItem item) {
        String processedText = new String();
        
        if (item.mCategory == "news") {
            if (item.mIsFirstItemOfCategoty)
                processedText = item.mProvider + "에서 제공하는 뉴스 정보입니다. "+ item.mTitle;
            else 
                processedText = item.mTitle;
            
        } else if (item.mCategory == "weather") {
            processedText = "기상청에서 제공하는 " + item.mProvider + " 정보입니다. "+ item.mTitle.replaceAll("\\(.*?\\)", "").replaceAll("~", "에서 ");
            
        } else if (item.mCategory == "rss") {
            if (item.mIsFirstItemOfCategoty)
                processedText = item.mProvider + "에서 제공하는 RSS 피드 정보입니다. "+ item.mTitle;
            else 
                processedText = item.mTitle;
        }
        
        return processedText;
    }
    
    public boolean isSpeaking() {
        return mTextToSpeech.isSpeaking();
    }
    
    public boolean isExplicitStop() {
        return mIsExplicitStop;
    }
    
    public int stop() {
        mIndex = 0;
        mIsExplicitStop = true;
        return mTextToSpeech.stop();
    }
    
    public void shutdown() {
        mTextToSpeech.shutdown();
    }
    
    public class SpeechItem {
        public String mCategory;
        public String mProvider;
        public String mTitle;
        public String mLink;
        public boolean mIsFirstItemOfCategoty;
        
        SpeechItem(String category, String provider, String title, String link, boolean isFirstItemOfCategoty) {
            mCategory = category;
            mProvider = provider;
            mTitle = title;
            mLink = link;
            mIsFirstItemOfCategoty = isFirstItemOfCategoty;
        }
    }
}
