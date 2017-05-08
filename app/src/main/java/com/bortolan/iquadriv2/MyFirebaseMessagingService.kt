package com.bortolan.iquadriv2

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.bortolan.iquadriv2.Activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var id: Int = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val data = remoteMessage!!.data
        Log.w("FIREBASE", "RECEIVE")

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("tab", R.id.tab_libri)
        intent.putExtra("query", data["isbn"].toString())
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val builder = NotificationCompat.Builder(applicationContext).setContentTitle(data["title"].toString()).setContentText(data["body"].toString()).setSmallIcon(R.mipmap.ic_launcher).setLights(Color.argb(255, 0, 0, 240), 1000, 1000).setAutoCancel(true)
        builder.setContentIntent(pendingIntent)

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // mId allows you to update the notification later on.
        id = PreferenceManager.getDefaultSharedPreferences(applicationContext).getInt("notification_id", 0)
        mNotificationManager.notify(id, builder.build())
        PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putInt("notification_id", id + 1).apply()
        super.onMessageReceived(remoteMessage)
    }
}
