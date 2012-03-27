package com.monkeylabs.morningcookie;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "[AlarmReceiver] Alarm received", Toast.LENGTH_LONG).show();
        
        Intent notify = new Intent(context, MorningCookieActivity.class);
        notify.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notify.putExtra("isAlarmActivated", true);
        PendingIntent sender = PendingIntent.getActivity(context, 0, notify, 0);
        try {
            sender.send();
        } catch(Exception e) {
            Toast.makeText(context, "[AlarmReceiver] Failed to send pending intent", Toast.LENGTH_SHORT).show();
        }
    }
}
