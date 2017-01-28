package com.bortolan.iquadriv2.Broadcasts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import static android.content.Context.ALARM_SERVICE;
import static com.bortolan.iquadriv2.Activities.MainActivity.NOTIFICATION_ID;
import static com.bortolan.iquadriv2.Utils.Methods.setAlarm;

public class OnBoot extends BroadcastReceiver {
    public OnBoot() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent operation = PendingIntent.getBroadcast(context, NOTIFICATION_ID, new Intent(context, Notifiche.class), 0);

        Log.d("NOTIFICATION", "BOOT RECEIVED - INTERVAL: " + preferences.getString("notify_frequency", String.valueOf(AlarmManager.INTERVAL_HOUR)));
        if (preferences.getBoolean("notify", true)) {
            if (preferences.getBoolean("notify_circolari", true) || preferences.getBoolean("notify_studenti", true)) {
                setAlarm(alarmManager, preferences, operation);
            }
        } else {
            alarmManager.cancel(operation);
        }
    }
}
