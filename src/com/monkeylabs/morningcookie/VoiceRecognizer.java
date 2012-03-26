package com.monkeylabs.morningcookie;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

public class VoiceRecognizer {
    private final String TAG = "VoiceRecognizer";
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 0;
    private final Context mContext;
    SpeechRecognizer mSpeechRecognizer;
    private boolean mEndOfSpeech;
    private float mRmsdB;
    
    
    VoiceRecognizer(Context context) {
        mContext = context;
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            public void onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech");
            }
            
            public void onBufferReceived(byte[] buffer) {
                Log.d(TAG, TAG + " onBufferReceived");
            }
    
            public void onEndOfSpeech() {
                Log.d(TAG, TAG + " onEndOfSpeech");
                mEndOfSpeech = true;
            }
            
            public void onError(int error) {
                Log.d(TAG, TAG + " onError: " + error);
                
                switch (error) {
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    ((MorningCookieActivity)mContext).effectManager().playVoiceRecognizerNoResponse();
                    ((MorningCookieActivity)mContext).textToSpeechStart();
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    ((MorningCookieActivity)mContext).effectManager().playVoiceRecognizerFail();
                    ((MorningCookieActivity)mContext).textToSpeechStart();
                    break;
                }
            }
            
            public void onEvent(int eventType, Bundle params) {
                Log.d(TAG, "onEvent");
            }
            
            public void onPartialResults(Bundle partialResults) {
                Log.d(TAG, "onPartialResults");
            }
            
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "onReadyForSpeech");
            }
            
            public void onResults(Bundle results) {
                Log.d(TAG, TAG + " onResults");
                ArrayList<String> matches = results.getStringArrayList("results_recognition");
                if (matches != null) {
                    Toast.makeText(mContext, "[VOICE_RECOGNITION_REQUEST_CODE] " + matches.toString(), Toast.LENGTH_LONG).show();
                    
                    for (String match : matches) {
                        if (match.equals("좋아요") || match.equals("좋아") || match.equals("조아요") || 
                            match.equals("좋아영") || match.equals("조아") || match.equals("조아영") || 
                            match.equals("like")) {
                            ((MorningCookieActivity)mContext).effectManager().playVoiceRecognizerSuccess();
                            break;
                        } else {
                            ((MorningCookieActivity)mContext).effectManager().playVoiceRecognizerFail();
                        }
                    }
                } else {
                    Toast.makeText(mContext, "[VOICE_RECOGNITION_REQUEST_CODE] No Match ", Toast.LENGTH_LONG).show();
                }
                
                ((MorningCookieActivity)mContext).textToSpeechStart();
            }
            
            public void onRmsChanged(float rmsdB) {
                Log.d(TAG, TAG + " onRmsChanged: " + rmsdB);
                mRmsdB = rmsdB;
            }
        });
    }
    
    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"com.tutorial.morningcookie");
        
        mEndOfSpeech = false;
        mRmsdB = 0;
        mSpeechRecognizer.startListening(intent);
        
        //((MorningCookieActivity)mContext).sendEmptyMessageDelyed(MorningCookieActivity.MSG_NO_RESPONSE_VOICE_RECOGNIZER, 3000);
        
        
        /*
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        
        ((MorningCookieActivity)mContext).startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    */}
    
    public void stop() {
        mSpeechRecognizer.stopListening();
    }
    
    public boolean hasResponse() {
        return mEndOfSpeech || mRmsdB > 5;
    }
}
