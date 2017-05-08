package com.bortolan.iquadriv2.Views

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.bortolan.iquadriv2.R

class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var titleTextView: TextView = view.findViewById(R.id.title) as TextView
    var priceTextView: TextView = view.findViewById(R.id.price) as TextView
    var isbnTextView: TextView = view.findViewById(R.id.isbn) as TextView
    var leftImage: View = view.findViewById(R.id.left_image)
    var main_content: View = view.findViewById(R.id.content)
    private val interpolator = DecelerateInterpolator(2f)


    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setPrice(price: Int) {
        priceTextView.text = String.format("€%s", price)
    }

    fun setISBN(ISBN: String) {
        isbnTextView.text = ISBN
    }

    internal fun setProgress(dX: Float) {
        val height = main_content.height.toFloat()
        val maxAbsXDiff = main_content.width / 2f
        val factor = interpolator.getInterpolation(Math.min(Math.abs(dX), maxAbsXDiff) / maxAbsXDiff)
        val diffX = factor * height

        main_content.translationX = diffX
        if (dX >= 0) {
            leftImage.alpha = factor
            leftImage.translationX = Math.abs(height - diffX) / -2f
        }
    }
}

