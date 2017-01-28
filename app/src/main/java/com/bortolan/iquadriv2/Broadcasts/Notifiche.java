package com.bortolan.iquadriv2.Broadcasts;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bortolan.iquadriv2.Activities.MainActivity;
import com.bortolan.iquadriv2.Interfaces.Circolare;
import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Utils.DownloadRSSFeed;

import static com.bortolan.iquadriv2.Utils.DownloadRSSFeed.CIRCOLARI;
import static com.bortolan.iquadriv2.Utils.DownloadRSSFeed.STUDENTI;
import static com.bortolan.iquadriv2.Utils.Methods.isNetworkAvailable;

public class Notifiche extends BroadcastReceiver {
    private int nNotif = 0;

    private final static String last_circolare = "last_circolare";
    private final static String last_studenti = "last_studenti";

    public Notifiche() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        download(preferences, context);
    }

    private void download(SharedPreferences preferences, Context context) {
        if (isNetworkAvailable(context)) {
            new DownloadRSSFeed(last_circolare, preferences, list -> checkUpdates(context, list.get(0), preferences, last_circolare, preferences.getBoolean("notify_circolari", true))).execute(CIRCOLARI);
            new DownloadRSSFeed(last_studenti, preferences, list -> checkUpdates(context, list.get(0), preferences, last_studenti, preferences.getBoolean("notify_studenti", true))).execute(STUDENTI);
        }
    }

    private void checkUpdates(Context context, Circolare firstItem, SharedPreferences preferences, String last_item_key_name, boolean notify) {
        if (notify) {
            if (!firstItem.getTitle().toLowerCase().trim().equals(preferences.getString(last_item_key_name, "").toLowerCase().trim())) {
                Log.d("NOTIFICATION", "NOTIFICATION");

                NotificationManagerCompat notificationManager;
                NotificationCompat.Builder mBuilder;

                String title = "";
                String content = "";
                PendingIntent intent = null;
                Intent i;
                switch (last_item_key_name) {
                    case "last_circolare":
                        title = "Quadri - Circolari";
                        content = "Nuove circolari da leggere";
                        i = new Intent(context, MainActivity.class);
                        i.putExtra("tab", R.id.tab_circolari);
                        intent = PendingIntent.getActivity(context, MainActivity.CIRCOLARI_ID, i, 0);
                        break;
                    case "last_studenti":
                        title = "Quadri degli Studenti";
                        content = "Nuovi post da leggere";
                        i = new Intent(context, MainActivity.class);
                        i.putExtra("tab", R.id.tab_avvisi);
                        intent = PendingIntent.getActivity(context, MainActivity.STUDENTI_ID, i, 0);
                        break;
                }

                mBuilder = new NotificationCompat.Builder(context)
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
                notificationManager.notify(nNotif++, mBuilder.build());

                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(last_item_key_name, firstItem.getTitle().toLowerCase().trim()).apply();
            }
        }
    }
}
