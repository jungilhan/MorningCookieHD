package com.tutorial.morningcookie;

import org.apache.cordova.DroidGap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class MorningCookieActivity extends DroidGap {
    private NativeAdapter mNativeAdapter;
    private TextToSpeechEngine mTextToSpeechEngine;
    private VoiceRecognizer mVoiceRecognizer;
    private SoundEffecter mEffectManager;
    private Handler mHandler;
    public final static int MSG_REQUEST_VOICE_RECOGNIZER = 0;
    public final static int MSG_NO_RESPONSE_VOICE_RECOGNIZER = 1;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.init();
        
        mTextToSpeechEngine = new TextToSpeechEngine(this);
        mVoiceRecognizer = new VoiceRecognizer(this);
        mEffectManager = new SoundEffecter(this);
        
        mNativeAdapter = new NativeAdapter(this, appView);
        appView.addJavascriptInterface(mNativeAdapter, "nativeAdapter");
        
        super.loadUrl("file:///android_asset/www/index.html");
        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MSG_REQUEST_VOICE_RECOGNIZER:
                    Toast.makeText(MorningCookieActivity.this, "[MSG_REQUEST_VOICE_RECOGNIZER]", Toast.LENGTH_LONG).show();
                    mEffectManager.playVoiceRecognizer();
                    mVoiceRecognizer.startVoiceRecognitionActivity();
                    this.sendEmptyMessageDelayed(MSG_NO_RESPONSE_VOICE_RECOGNIZER, 3000);
                    break;
                    
                case MSG_NO_RESPONSE_VOICE_RECOGNIZER:
                    if (!mVoiceRecognizer.hasResponse())
                        mVoiceRecognizer.stop();
                    break;
                }
            }
        };
    }
    
    public TextToSpeechEngine textToSpeechEngine() {
        return mTextToSpeechEngine;
    }
    
    public void textToSpeechStart() {
        if (!mTextToSpeechEngine.isExplicitStop() && !mTextToSpeechEngine.isSpeaking()) {
            mTextToSpeechEngine.speak();
        }
    }
    
    public SoundEffecter effectManager() {
        return mEffectManager;
    }
    
    public void sendMessage(int what, Object obj) {
        mHandler.sendMessage(Message.obtain(mHandler, what, obj));
    }
    
    public void sendEmptyMessageDelyed(int what, long delayMillis) {
        mHandler.sendEmptyMessageDelayed(what, delayMillis);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (requestCode == VoiceRecognizer.VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(MorningCookieActivity.this, "[VOICE_RECOGNITION_REQUEST_CODE] " + matches.toString(), Toast.LENGTH_LONG).show();
            
            for (String match : matches) {
                if (match.equals("좋아요") || match.equals("좋아") || match.equals("조아요") || 
                    match.equals("좋아영") || match.equals("조아") || match.equals("조아영") || 
                    match.equals("like")) {
                    mEffectManager.playVoiceRecognizerSuccess();
                    break;
                } else {
                    mEffectManager.playVoiceRecognizerFail();
                }
            }
        }
        
        mTextToSpeechEngine.speak();
        super.onActivityResult(requestCode, resultCode, data);
        */
    }
    
    @Override
    public void onDestroy() {
        mTextToSpeechEngine.stop();
        mTextToSpeechEngine.shutdown();
        
        super.onDestroy();
    }
}