package com.monkeylabs.morningcookie;

import java.util.Calendar;

import org.apache.cordova.DroidGap;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.googlecode.android.widgets.DateSlider.DateSlider;
import com.googlecode.android.widgets.DateSlider.TimeSlider;

public class MorningCookieActivity extends DroidGap {
    private NativeAdapter mNativeAdapter;
    private TextToSpeechEngine mTextToSpeechEngine;
    private VoiceRecognizer mVoiceRecognizer;
    private SoundEffecter mEffectManager;
    private Handler mHandler;
    public final static int MSG_REQUEST_VOICE_RECOGNIZER = 0;
    public final static int MSG_NO_RESPONSE_VOICE_RECOGNIZER = 1;
    public final static int MSG_REQUEST_TIME_PICKER = 2;
    
    public final static int ID_TIME_PICKER_DIALOG = 0;
    
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
    protected Dialog onCreateDialog(int id) {
        final Calendar c = Calendar.getInstance();
        
        switch (id) {
        case ID_TIME_PICKER_DIALOG:
            return new TimeSlider(this, new DateSlider.OnDateSetListener() {
                public void onDateSet(DateSlider view, Calendar selectedDate) {
                    String time = String.format("%tR", selectedDate);
                    //Toast.makeText(MorningCookieActivity.this, "다음 시간에 알람을 설정했습니다! " + time, Toast.LENGTH_SHORT).show();
                    mNativeAdapter.sendMessage("onTimepickerRequestComplete", Helper.ampmChanger(time), " - Mon Tue Wed Thu Fri Sat Sun");
                }}, c, 5);
        }
        
        return null;
    }
    
    @Override
    public void onDestroy() {
        mTextToSpeechEngine.stop();
        mTextToSpeechEngine.shutdown();
        
        mVoiceRecognizer.stop();
        mVoiceRecognizer.destroy();
        
        super.onDestroy();
    }
}