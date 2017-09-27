package com.bortolan.iquadriv2.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bortolan.iquadriv2.Activities.ActivityMain;
import com.bortolan.iquadriv2.Interfaces.Circolare;
import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Tasks.Cache.CacheListTask;
import com.bortolan.iquadriv2.Tasks.Remote.DownloadArticles;

import java.util.List;

import kotlin.Unit;

import static com.bortolan.iquadriv2.Utils.Methods.isNetworkAvailable;

public class QDSNotification extends Service {
    private final static String last_circolare = "last_circolare";
    private final static String TAG = "QDSNotification";
    private final static String last_studenti = "last_studenti";
    Intent intent;
    private int nNotif = 978;

    public QDSNotification() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        download(preferences, this);
        this.intent = intent;
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void download(SharedPreferences preferences, Context context) {
        if (isNetworkAvailable(context)) {
            new DownloadArticles(list -> {
                Log.d(TAG, "Download Size: " + list.size());
                if (!list.isEmpty()) {
                    Log.d(TAG, list.get(0).getTitle());
                    checkUpdates(context, list.get(0), preferences, last_studenti, preferences.getBoolean("notify_studenti", true));
                    new CacheListTask(context.getCacheDir(), "Studenti").execute((List) list);
                }
                return Unit.INSTANCE;
            }).execute();
        }
    }

    private void checkUpdates(Context context, Circolare firstItem, SharedPreferences preferences, String last_item_key_name, boolean notify) {
        if (notify) {
            if (!firstItem.getTitle().toLowerCase().trim().equals(preferences.getString(last_item_key_name, "").toLowerCase().trim())) {
                Log.w(TAG, "Shoot Notification -> " + firstItem.getTitle());

                NotificationManagerCompat notificationManager;
                NotificationCompat.Builder mBuilder;

                String title = "Quadri degli Studenti";
                String content = "Nuovi post da leggere";
                Intent i = new Intent(context, ActivityMain.class);
                i.putExtra("tab", R.id.tab_avvisi);
                PendingIntent intent = PendingIntent.getActivity(context, ActivityMain.STUDENTI_ID, i, 0);

                mBuilder = new NotificationCompat.Builder(context, "iQuadri")
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentText(content)
                        .setContentTitle(title)
                        .setContentIntent(intent)
                        .setLights(Color.BLUE, 3000, 3000)
                        .setAutoCancel(true);

                if (preferences.getBoolean("notify_vibrate", true))
                    mBuilder.setVibrate(new long[]{250, 250, 250, 250});
                if (preferences.getBoolean("notify_sound", true))
                    mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(nNotif, mBuilder.build());

                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(last_item_key_name, firstItem.getTitle().toLowerCase().trim()).apply();
                this.stopService(this.intent);
            }
        }
    }
}
