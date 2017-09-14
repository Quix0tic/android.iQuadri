package com.bortolan.iquadriv2.Widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.bortolan.iquadriv2.Databases.FavouritesDB
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Utils.Methods
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

/**
 * Implementation of App Widget functionality.
 */
class WidgetOrario : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        Log.d("WIDGET", "CREATED")
        //TODO: select orario if there aren't favourites
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d("WIDGET", "DESTROYED")
    }

    companion object {
        val handler = Handler(Looper.getMainLooper())
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_orario)

            Log.d("WIDGET", "UPDATE, LOAD: " + FavouritesDB.getInstance(context).all[0].url)

            handler.post {
                Picasso.with(context).load("http://wp.liceoquadri.it/wp-content/archivio/orario/" + FavouritesDB.getInstance(context).all[0].url).resize(Methods.getDisplaySize(context).x, 0).onlyScaleDown().into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        views.setViewVisibility(R.id.progress_bar, View.VISIBLE)
                        views.setViewVisibility(R.id.photo, View.GONE)
                        views.setViewVisibility(R.id.text_error, View.GONE)
                        handler.post { appWidgetManager.updateAppWidget(appWidgetId, views) }
                        Log.d("WIDGET", "BITMAP LOADING")
                    }

                    override fun onBitmapFailed(errorDrawable: Drawable?) {
                        views.setViewVisibility(R.id.progress_bar, View.GONE)
                        views.setViewVisibility(R.id.photo, View.GONE)
                        views.setViewVisibility(R.id.text_error, View.VISIBLE)
                        handler.post { appWidgetManager.updateAppWidget(appWidgetId, views) }
                        Log.d("WIDGET", "BITMAP NOT LOADED")
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        views.setViewVisibility(R.id.progress_bar, View.GONE)
                        views.setViewVisibility(R.id.photo, View.VISIBLE)
                        views.setViewVisibility(R.id.text_error, View.GONE)
                        views.setImageViewBitmap(R.id.photo, bitmap)
                        // Instruct the widget manager to update the widget
                        handler.post { appWidgetManager.updateAppWidget(appWidgetId, views) }
                        Log.d("WIDGET", "BITMAP LOADED FROM " + from?.name)
                    }

                })

            }
        }
    }
}

