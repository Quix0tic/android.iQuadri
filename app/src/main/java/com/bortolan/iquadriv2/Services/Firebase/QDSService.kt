package com.bortolan.iquadriv2.Services.Firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.bortolan.iquadriv2.Activities.ActivityMain
import com.bortolan.iquadriv2.Interfaces.Circolare
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Tasks.Cache.CacheListTask
import com.bortolan.iquadriv2.Tasks.Remote.DownloadArticles
import com.bortolan.iquadriv2.Utils.Methods
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

class QDSService : JobService() {
    override fun onStartJob(job: JobParameters?): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (Methods.isNetworkAvailable(applicationContext)) {
            DownloadArticles { list ->
                if (list == null || list.isEmpty()) return@DownloadArticles

                Log.d("QDSService", list[0].title)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    checkUpdatesV26(applicationContext, list[0], preferences, last_circolare, preferences.getBoolean("notify_circolari", true))
                } else {
                    checkUpdates(applicationContext, list[0], preferences, last_circolare, preferences.getBoolean("notify_circolari", true))
                }
                CacheListTask(applicationContext.cacheDir, "Circolari").execute(list)

                return@DownloadArticles
            }.execute()
        }
        return false
    }

    override fun onStopJob(job: JobParameters?): Boolean {
        return false
    }

    @RequiresApi(26)
    private fun checkUpdatesV26(context: Context, firstItem: Circolare, preferences: SharedPreferences, last_item_key_name: String, notify: Boolean) {
        if (notify) {
            //TO-DO: uncomment
            if (!firstItem.title.toLowerCase().trim().equals(preferences.getString(last_item_key_name, "").toLowerCase().trim())) {
                Log.w("QDSService", "Shoot Notification -> " + firstItem.title)

                val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
                val mBuilder: Notification.Builder

                val title = "Quadri degli Studenti"
                val content = "Nuovi post da leggere"
                val i = Intent(context, ActivityMain::class.java)
                i.putExtra("tab", R.id.tab_circolari)
                val intent = PendingIntent.getActivity(context, ActivityMain.CIRCOLARI_ID, i, 0)

                mBuilder = Notification.Builder(context, if (preferences.getBoolean("notify_sound", true)) channelId else channelId_mute)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentText(content)
                        .setContentTitle(title)
                        .setContentIntent(intent)
                        .setAutoCancel(true)

                val channel = NotificationChannel(channelId, "iQuadri", NotificationManager.IMPORTANCE_HIGH)
                channel.enableLights(true)
                channel.enableVibration(preferences.getBoolean("notify_vibrate", true))
                channel.lightColor = Color.BLUE

                val channelMute = NotificationChannel(channelId_mute, "iQuadri silent", NotificationManager.IMPORTANCE_LOW)
                channelMute.enableLights(true)
                channelMute.enableVibration(preferences.getBoolean("notify_vibrate", true))
                channelMute.lightColor = Color.BLUE

                if (preferences.getBoolean("notify_vibrate", true)) {
                    channel.vibrationPattern = longArrayOf(250, 250, 250, 250)
                }
                if (preferences.getBoolean("notify_sound", true)) {
                    channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_NOTIFICATION).build())
                }

                notificationManager.createNotificationChannel(channel)
                notificationManager.createNotificationChannel(channelMute)
                notificationManager.notify(nNotif, mBuilder.build())

                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(last_item_key_name, firstItem.title.toLowerCase().trim { it <= ' ' }).apply()
            }
        }
    }

    private fun checkUpdates(context: Context, firstItem: Circolare, preferences: SharedPreferences, last_item_key_name: String, notify: Boolean) {
        if (notify) {
            if (firstItem.title.toLowerCase().trim { it <= ' ' } != preferences.getString(last_item_key_name, "")!!.toLowerCase().trim { it <= ' ' }) {
                Log.w("QDSService", "Shoot Notification -> " + firstItem.title)

                val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
                val mBuilder: NotificationCompat.Builder

                val title = "Quadri degli Studenti"
                val content = "Nuovi post da leggere"
                val i = Intent(context, ActivityMain::class.java)
                i.putExtra("tab", R.id.tab_avvisi)
                val intent = PendingIntent.getActivity(context, ActivityMain.STUDENTI_ID, i, 0)

                mBuilder = NotificationCompat.Builder(context, "iQuadri")
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentText(content)
                        .setContentTitle(title)
                        .setContentIntent(intent)
                        .setLights(Color.BLUE, 3000, 3000)
                        .setAutoCancel(true)

                if (preferences.getBoolean("notify_vibrate", true))
                    mBuilder.setVibrate(longArrayOf(250, 250, 250, 250))
                if (preferences.getBoolean("notify_sound", true))
                    mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                notificationManager.notify(nNotif, mBuilder.build())

                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(last_item_key_name, firstItem.title.toLowerCase().trim { it <= ' ' }).apply()
            }
        }
    }

    companion object {
        private const val nNotif: Int = 977
        private val last_circolare = "last_circolare"
        private val channelId = "iquadri_channel_03"
        private val channelId_mute = "iquadri_channel_04"
    }
}