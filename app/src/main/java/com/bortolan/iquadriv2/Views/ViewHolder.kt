package com.bortolan.iquadriv2.Views

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bortolan.iquadriv2.R

class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    @BindView(R.id.title)
    internal var titleTextView: TextView? = null
    @BindView(R.id.price)
    internal var priceTextView: TextView? = null
    @BindView(R.id.isbn)
    internal var isbnTextView: TextView? = null
    @BindView(R.id.left_image)
    internal var leftImage: View? = null
    @BindView(R.id.content)
    internal var main_content: View? = null
    private val interpolator = DecelerateInterpolator(2f)

    init {
        ButterKnife.bind(this, view)
    }

    fun setTitle(title: String) {
        titleTextView!!.text = title
    }

    fun setPrice(price: Int) {
        priceTextView!!.text = String.format("€%s", price)
    }

    fun setISBN(ISBN: String) {
        isbnTextView!!.text = ISBN
    }

    internal fun setProgress(dX: Float) {
        val height = main_content!!.height.toFloat()
        val maxAbsXDiff = main_content!!.width / 2f
        val factor = interpolator.getInterpolation(Math.min(Math.abs(dX), maxAbsXDiff) / maxAbsXDiff)
        val diffX = factor * height

        main_content!!.translationX = diffX
        if (dX >= 0) {
            leftImage!!.alpha = factor
            leftImage!!.translationX = Math.abs(height - diffX) / -2f
        }
    }
}

