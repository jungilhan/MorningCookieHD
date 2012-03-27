package com.monkeylabs.morningcookie;

import java.util.Calendar;

import org.apache.cordova.DroidGap;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.googlecode.android.widgets.DateSlider.DateSlider;
import com.googlecode.android.widgets.DateSlider.TimeSlider;

public class MorningCookieActivity extends DroidGap {
    private NativeAdapter mNativeAdapter;
    private TextToSpeechEngine mTextToSpeechEngine;
    private VoiceRecognizer mVoiceRecognizer;
    private SoundEffecter mSoundEffecter;
    private FacebookAdapter mFacebookAdapter;
    private Handler mHandler;
    private Object mSpeechItem;
    
    public final static int MSG_VOICE_RECOGNIZER_REQUEST = 0;
    public final static int MSG_VOICE_RECOGNIZER_NO_RESPONSE = 1;
    public final static int MSG_VOICE_RECOGNIZER_NO_MATCH = 2;
    public final static int MSG_VOICE_RECOGNIZER_TIMEOUT = 3;
    public final static int MSG_VOICE_RECOGNIZER_MATCH_SUCCEED = 4;
    public final static int MSG_VOICE_RECOGNIZER_MATCH_FAILED = 5;    
    public final static int MSG_TIME_PICKER_REQUEST = 10;
    
    public final static int ID_TIME_PICKER_DIALOG = 0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.init();
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        
        mTextToSpeechEngine = new TextToSpeechEngine(this);
        mVoiceRecognizer = new VoiceRecognizer(this);
        mSoundEffecter = new SoundEffecter(this);
        mFacebookAdapter = new FacebookAdapter(this); 
        
        mNativeAdapter = new NativeAdapter(this, appView);
        appView.addJavascriptInterface(mNativeAdapter, "nativeAdapter");
        
        super.loadUrl("file:///android_asset/www/index.html");
        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MSG_VOICE_RECOGNIZER_REQUEST:
                    //Toast.makeText(MorningCookieActivity.this, "[MSG_VOICE_RECOGNIZER_REQUEST]", Toast.LENGTH_LONG).show();
                    mSpeechItem = msg.obj;
                    
                    mSoundEffecter.playVoiceRecognizer();
                    mVoiceRecognizer.startVoiceRecognition();
                    this.sendEmptyMessageDelayed(MSG_VOICE_RECOGNIZER_NO_RESPONSE, 3000);
                    break;
                    
                case MSG_VOICE_RECOGNIZER_NO_RESPONSE:
                    if (!mVoiceRecognizer.hasResponse()) {
                        Log.i(TAG, "[VoiceRecognize Result] MSG_VOICE_RECOGNIZER_NO_RESPONSE");
                        mVoiceRecognizer.stop(); // STOP 처리 시 TIMEOUT 에러가 발생되므로 여기서는 textToSpeechStart() 호출하지 않음.
                    }
                    break;
                    
                case MSG_VOICE_RECOGNIZER_NO_MATCH:
                case MSG_VOICE_RECOGNIZER_MATCH_FAILED:
                    Log.i(TAG, "[VoiceRecognize Result] MSG_VOICE_RECOGNIZER_NO_MATCH | MSG_VOICE_RECOGNIZER_MATCH_FAILED");
                    mSoundEffecter.playVoiceRecognizerFail();
                    textToSpeechStart();
                    break;
                    
                case MSG_VOICE_RECOGNIZER_TIMEOUT:
                    Log.i(TAG, "[VoiceRecognize Result] MSG_VOICE_RECOGNIZER_TIMEOUT");
                    mSoundEffecter.playVoiceRecognizerNoResponse();
                    textToSpeechStart();
                    break;
                    
                case MSG_VOICE_RECOGNIZER_MATCH_SUCCEED:
                    Log.i(TAG, "[VoiceRecognize Result] MSG_VOICE_RECOGNIZER_MATCH_SUCCEED");
                    mSoundEffecter.playVoiceRecognizerSuccess();
                    mFacebookAdapter.addPostQueue(mSpeechItem);
                    textToSpeechStart();
                    break;
                }
            }
        };
        
        boolean isAlarmActivated = isAlarmActivated(getIntent());
        Toast.makeText(MorningCookieActivity.this, "[onCreate] isAlarmActivated: " + isAlarmActivated, Toast.LENGTH_LONG).show();
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
        return mSoundEffecter;
    }
    
    public FacebookAdapter facebookAdapter() {
        return mFacebookAdapter;
    }
    
    public void sendMessage(int what, Object obj) {
        mHandler.sendMessage(Message.obtain(mHandler, what, obj));
    }
    
    public void sendEmptyMessageDelyed(int what, long delayMillis) {
        mHandler.sendEmptyMessageDelayed(what, delayMillis);
    }
    
    private boolean isAlarmActivated(Intent intent) {
        return intent.getBooleanExtra("isAlarmActivated", false);
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
                    
                    // TODO Alarm.
                    Intent intent = new Intent(MorningCookieActivity.this, AlarmReceiver.class);
                    PendingIntent sender = PendingIntent.getBroadcast(MorningCookieActivity.this, 0, intent, 0);
                    
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 5);
                    
                    AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                }}, c, 5);
        }
        
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFacebookAdapter.authorizeCallback(requestCode, resultCode, data);
        Toast.makeText(MorningCookieActivity.this, "페이스북 인증이 완료됐습니다! " + resultCode + " isSessionValid: " + mFacebookAdapter.isSessionValid(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        boolean isAlarmActivated = isAlarmActivated(intent);
        Toast.makeText(MorningCookieActivity.this, "[onNewIntent] isAlarmActivated: " + isAlarmActivated, Toast.LENGTH_LONG).show();
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