package com.bortolan.iquadriv2.Views

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import com.transitionseverywhere.TransitionManager

class ZoomableImageView(val c: Context) : ImageView(c) {
    var g: GestureDetector = GestureDetector(c, GestureListener())
    var fit = true

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return g.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        // event when double tap occurs
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val x = e.x
            val y = e.y

            Log.d("Double Tap", "Tapped at: ($x,$y)")
            fit = !fit
            TransitionManager.beginDelayedTransition(parent as ViewGroup?)
            if (fit) {
                scaleType = ScaleType.FIT_CENTER
            } else {
                scaleType = ScaleType.CENTER_CROP

            }

            return true
        }
    }
}