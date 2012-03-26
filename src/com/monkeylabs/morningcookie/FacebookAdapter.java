package com.monkeylabs.morningcookie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.monkeylabs.morningcookie.TextToSpeechEngine.SpeechItem;

public class FacebookAdapter {
    private final String TAG = "FacebookAdapter";
    private final Context mContext;
    private final Facebook mFacebook;
    private final AsyncFacebookRunner mAsyncRunner;
    private final Queue mLikeItemQueue;
    private final Handler mHandler;
    
    public final static int MSG_ON_TIMER = 0;
    
    FacebookAdapter(Context context) {
        mContext = context;
        mFacebook = new Facebook("348248981879722");
        mAsyncRunner = new AsyncFacebookRunner(mFacebook);
        mLikeItemQueue = new LinkedList();
        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MSG_ON_TIMER:
                    Log.d(TAG, "[FacebookAdapter.MSG_ON_TIMER] " + mLikeItemQueue.size());
                    if (mLikeItemQueue.size() > 0 && !isSessionValid()) {
                            authorize();
                            Log.d(TAG, "[FacebookAdapter.MSG_ON_TIMER] Do Authrize!!");
                            
                        } else if (mLikeItemQueue.size() > 0 && isSessionValid()) {
                            SpeechItem item = (SpeechItem)mLikeItemQueue.poll();
                            FacebookAdapter.this.post(item.mTitle, item.mLink, null);
                            
                            Log.d(TAG, "[FacebookAdapter.MSG_ON_TIMER] " + item.mTitle + " " + item.mLink);
                        }
                    
                    startOneShotTimer();
                    break;
                }
            }
        };
        
        startOneShotTimer();
    }

    private void startOneShotTimer() {
        mHandler.sendEmptyMessageDelayed(MSG_ON_TIMER, 2000);
    }
    
    public void login() {
        if (!isSessionValid()) {
            authorize();
        } else {
            Toast.makeText(mContext, "이미 페이스북에 로그인 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void logout() {
        if (isSessionValid()) {
            AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
            asyncRunner.logout(mContext, new RequestListener() {
                public void onComplete(String response, Object state) {
                    Log.d(TAG, "[TAG.logout] onComplete");
                }
         
                public void onIOException(IOException e, Object state) {
                    Log.d(TAG, "[FacebookAdapter.logout] onIOException");
                }
         
                public void onFileNotFoundException(FileNotFoundException e, Object state) {
                    Log.d(TAG, "[FacebookAdapter.logout] onFileNotFoundException");
                }
         
                public void onMalformedURLException(MalformedURLException e, Object state) {
                    Log.d(TAG, "[FacebookAdapter.logout] onMalformedURLException");
                }
         
                public void onFacebookError(FacebookError e, Object state) {
                    Log.d(TAG, "[FacebookAdapter.logout] onFacebookError");
                }
            }); 
        } else {
            Toast.makeText(mContext, "이미 페이스북에 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    
    public boolean isSessionValid() {
        return mFacebook.isSessionValid();
    }
    
    private void authorize() {
        mFacebook.authorize((Activity)mContext, new String[] { "publish_stream", "read_stream", "offline_access" }, new Facebook.DialogListener() {
            public void onComplete(Bundle values) {
                Log.d(TAG, "[FacebookAdapter.authorize] onComplete");
            }

            public void onFacebookError(FacebookError e) {
                Log.d(TAG, "[FacebookAdapter.authorize] onFacebookError: " + e.getMessage());
            }

            public void onError(DialogError e) {
                Log.d(TAG, "[FacebookAdapter.authorize] onError: " + e.getMessage());
            }

            public void onCancel() {
                Log.d(TAG, "[FacebookAdapter.authorize] onCancel");
            }
        });
    }
    
    public void authorizeCallback(int requestCode, int resultCode, Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    public void addPostQueue(Object item) {
        mLikeItemQueue.offer(item);
    }
    
    private void post(String message, String link, String picture) {
        Bundle params = new Bundle();
        
        if (message != null && message.length() > 0)
            params.putString("message", "모닝 쿠키에서 다음 정보를 공유했습니다.\n" + message);
        
        if (link != null && link.length() > 0)
            params.putString("link", link);
        
        if (picture != null && picture.length() > 0)
            params.putString("picture", picture);
        
        try {
            mAsyncRunner.request("me/feed", params, "POST", new AsyncFacebookRunner.RequestListener() {
                public void onMalformedURLException(MalformedURLException e, Object state) {
                    
                }
                
                public void onIOException(IOException e, Object state) {
                    
                }
                
                public void onFileNotFoundException(FileNotFoundException e, Object state) {
                    
                }
                
                public void onFacebookError(FacebookError e, Object state) {
                    
                }
                
                public void onComplete(final String response, final Object state) {
                    try {
                        // process the response here: (executed in background thread)
                        Log.d(TAG, "[FacebookAdapter.request.onComplete] Response: " + response.toString());
                        JSONObject json = Util.parseJson(response);
                        //final String src = json.getString("src");

                        // then post the processed result back to the UI thread
                        // if we do not do this, an runtime exception will be generated
                        // e.g. "CalledFromWrongThreadException: Only the original
                        // thread that created a view hierarchy can touch its views."
                        ((MorningCookieActivity)mContext).runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(mContext, "[FacebookAdapter Post]" + " 페이스북에 성공적으로 포스팅 되었습니다!", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (JSONException e) {
                        Log.d(TAG, "[FacebookAdapter.request.onComplete] JSON Error in response: " + e.getMessage());
                    } catch (FacebookError e) {
                        Log.d(TAG, "[FacebookAdapter.request.onComplete] Facebook Error: " + e.getMessage());
                    }
                }
            }, null);
            
        } catch (Exception e) {
            Toast.makeText(mContext, "[FacebookAdapter Exception] " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
