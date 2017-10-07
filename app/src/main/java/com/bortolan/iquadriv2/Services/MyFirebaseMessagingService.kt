package com.bortolan.iquadriv2.Services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.bortolan.iquadriv2.Activities.ActivityMain
import com.bortolan.iquadriv2.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var id: Int = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val data = remoteMessage!!.data
        Log.w("FIREBASE", "RECEIVE")
        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        if (!data["isbn"].isNullOrEmpty()) {

            val intent = Intent(this, ActivityMain::class.java)
            intent.putExtra("tab", R.id.tab_libri)
            intent.putExtra("query", data["isbn"].toString())
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT)
            val builder = NotificationCompat.Builder(applicationContext, "iQuadri").setContentTitle(data["title"].toString()).setContentText(data["body"].toString()).setSmallIcon(R.mipmap.ic_launcher).setLights(Color.argb(255, 0, 0, 240), 1000, 1000).setAutoCancel(true)
            builder.setContentIntent(pendingIntent)

            val mNotificationManager = getSystemService(NotificationManager::class.java)
            // mId allows you to update the notification later on.
            id = PreferenceManager.getDefaultSharedPreferences(applicationContext).getInt("notification_id", 0)
            mNotificationManager.notify(id, builder.build())
            PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putInt("notification_id", id + 1).apply()
        } else if (pref.getBoolean("notify", true)) {
            Log.w("FirebaseService", "Shoot Notification")

            if (data["action"]?.equals("circolari") == true && pref.getBoolean("circolari", true)) {
                startService(Intent(this, CircolariNotification::class.java))
            } else if (data["action"]?.equals("studenti") == true && pref.getBoolean("studenti", true)) {
                startService(Intent(this, QDSNotification::class.java))

            }
        }

    }
}
