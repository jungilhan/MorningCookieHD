package com.monkeylabs.morningcookie;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundEffecter {
    private final Context mContext;
    private final SoundPool mSoundPool;
    private final int mStartEffect;
    private final int mSuccessEffect;
    private final int mFailEffect;
    private final int mNoResponseEffect;
    
    SoundEffecter(Context context) {
        mContext = context;
        
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mStartEffect = mSoundPool.load(mContext, R.raw.start_effect, 1);
        mSuccessEffect = mSoundPool.load(mContext, R.raw.success_effect, 1);
        mFailEffect = mSoundPool.load(mContext, R.raw.fail_effect, 1);
        mNoResponseEffect = mSoundPool.load(mContext, R.raw.noresponse_effect, 1);
        
    }
    
    public void playVoiceRecognizer() {
        mSoundPool.play(mStartEffect, 1, 1, 0, 0, 1);
    }
    
    public void playVoiceRecognizerSuccess() {
        mSoundPool.play(mSuccessEffect, 1, 1, 0, 0, 1);
    }
    
    public void playVoiceRecognizerFail() {
        mSoundPool.play(mFailEffect, 1, 1, 0, 0, 1);
    }
    
    public void playVoiceRecognizerNoResponse() {
        mSoundPool.play(mNoResponseEffect, 1, 1, 0, 0, 1);
    }
}
