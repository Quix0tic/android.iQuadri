package com.bortolan.iquadriv2.Activities.IntentFilter

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Widget.WidgetOrario


class ActivityChooseOrario : AppCompatActivity() {
    private var widgetId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_orario)

        widgetId = intent.getIntExtra("widget_id", -1)


    }

    fun updateWidgetAndClose(widgetId: Int, url: String) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(widgetId.toString(), url).apply()
        val intent = Intent(this, WidgetOrario::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = intArrayOf(widgetId)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
        finish()
    }
}
