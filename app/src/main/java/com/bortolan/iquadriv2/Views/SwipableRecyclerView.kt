package com.bortolan.iquadriv2.Views

import android.content.Context
import android.graphics.Canvas
import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE
import android.util.AttributeSet
import com.bortolan.iquadriv2.Views.SwipableRecyclerView.OnSwipeActionListener.Companion.RIGHT
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension

class SwipableRecyclerView : RecyclerView {
    internal var listener: OnSwipeActionListener? = null

    constructor(context: Context) : super(context) {
        setupSwipeGesture()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupSwipeGesture()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        setupSwipeGesture()
    }

    private fun setupSwipeGesture() {
        val simpleItemTouchCallback = createSwipeItemTouchHelper()
        val itemTouchHelper = ItemTouchHelperExtension(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(this)
    }

    private fun createSwipeItemTouchHelper(): ItemTouchHelperExtension.Callback {
        return object : ItemTouchHelperExtension.Callback() {

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return ItemTouchHelperExtension.Callback.makeMovementFlags(0, ItemTouchHelper.END)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                //Reset the state
                adapter.notifyItemChanged(position)
                if (listener != null) {
                    listener!!.onSwipe(position, RIGHT)
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                if (viewHolder is com.bortolan.iquadriv2.Views.ViewHolder) {
                    if (actionState == ACTION_STATE_SWIPE) {
                        viewHolder.handleSwipeGesture(dX)
                    }
                }
            }
        }
    }

    internal interface OnSwipeActionListener {

        fun onSwipe(position: Int, @SwipeDirection direction: Int)

        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        @IntDef(LEFT.toLong(), RIGHT.toLong())
        annotation class SwipeDirection

        companion object {
            const val LEFT = 0
            const val RIGHT = 1
        }
    }

}
