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
                                    mainActivity.sendMessage(MorningCookieActivity.MSG_REQUEST_VOICE_RECOGNIZER, null);
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
        return mTextToSpeech.speak(item.mTitle, TextToSpeech.QUEUE_FLUSH, params);
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
        public String mTitle;
        public String mLink;
        
        SpeechItem(String category, String title, String link) {
            mCategory = category;
            mTitle = title;
            mLink = link;
        }
    }
}
