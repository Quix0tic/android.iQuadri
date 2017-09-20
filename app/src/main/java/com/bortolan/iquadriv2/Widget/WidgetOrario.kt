package com.bortolan.iquadriv2.Widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import com.bortolan.iquadriv2.Databases.FavouritesDB
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Utils.Methods
import com.squareup.picasso.Picasso

/**
 * Implementation of App Widget functionality.
 */
class WidgetOrario : AppWidgetProvider() {
    val favourites: MutableList<GitHubItem> = arrayListOf()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        favourites.clear()
        favourites.addAll(FavouritesDB.getInstance(context).all)

        for (appWidgetId in appWidgetIds) {
            val i = appWidgetIds.indexOf(appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId, false)
        }
    }

    companion object {
        val handler = Handler(Looper.getMainLooper())

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int, needOpenActivity: Boolean) {
            val views = RemoteViews(context.packageName, R.layout.widget_orario)

            var url = "http://wp.liceoquadri.it/wp-content/archivio/orario/" + FavouritesDB.getInstance(context).all[0].url
            handler.post {
                Picasso.with(context).load(url).resize(Methods.getDisplaySize(context).x, 0).onlyScaleDown().into(views, R.id.photo, intArrayOf(appWidgetId))
            }

        }
    }
}

