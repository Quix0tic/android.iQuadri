package com.bortolan.iquadriv2.Views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.bortolan.iquadriv2.Adapters.AdapterOrari
import com.bortolan.iquadriv2.Databases.FavouritesDB
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem
import com.bortolan.iquadriv2.R
import kotlinx.android.synthetic.main.view_orari.view.*
import java.util.*

open class ScheduleList : LinearLayout {
    internal var updateFragment: AdapterOrari.UpdateFragment? = null
    private var mContext: Context? = null
    private var adapter: AdapterOrari? = null
    private var items: MutableList<GitHubItem>? = null
    private var order: Boolean = false

    internal var db: FavouritesDB? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        init()
    }

    fun init() {
        View.inflate(mContext, R.layout.view_orari, this)
        items = LinkedList<GitHubItem>()

        recycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        recycler.setHasFixedSize(true)
        recycler.layoutManager.isAutoMeasureEnabled = true
    }

    fun setData(title: String?, items: List<GitHubItem>?, db: FavouritesDB?, updateFragment: AdapterOrari.UpdateFragment?, order: Boolean) {
        this.db = db
        this.updateFragment = updateFragment
        this.order = order

        if (adapter != null) adapter!!.clear()
        this.items!!.clear()
        if (title != null)
            this.title!!.text = title.toUpperCase()
        if (items != null && items.isNotEmpty()) {
            if (order) {
                Collections.sort(items) { o1, o2 -> o1.name.compareTo(o2.name, ignoreCase = true) }
            }
            this.items!!.addAll(items)

            adapter = AdapterOrari(mContext, db, updateFragment)
            adapter!!.addAll(items)
            recycler!!.adapter = adapter
            recycler!!.addOnScrollListener(myScrollListener(this.title))

            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }

    fun remove(position: Int) {
        if (position >= 0 && position < items!!.size) {
            items!!.removeAt(position)
            adapter!!.remove(position)

            visibility = if (items!!.size > 0) View.VISIBLE else View.GONE
        }
    }

    fun add(t: GitHubItem, position: Int) {
        items!!.add(position, t)
        if (adapter == null) {
            setData(null, listOf(t), db, updateFragment, order)
        } else {
            adapter!!.add(t, position)
            visibility = if (items!!.size > 0) View.VISIBLE else View.GONE
        }
    }

    fun filter(s: String) {
        if (adapter != null)
            adapter!!.filter.filter(s)
    }


    protected inner class myScrollListener internal constructor(private val textView: TextView?) : RecyclerView.OnScrollListener() {
        internal var _text: String = textView!!.text.toString()
        internal var myState: Int = 0
        internal var firstVisible: Int = 0

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            myState = newState
            if (myState == RecyclerView.SCROLL_STATE_IDLE) textView?.text = _text
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (myState != RecyclerView.SCROLL_STATE_IDLE) {
                firstVisible = (recyclerView!!.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (firstVisible >= 0 && firstVisible < recyclerView.adapter.itemCount)
                    textView?.text = String.format("%1s - %2s", _text, (recyclerView.adapter as AdapterOrari).getItem(firstVisible).name.substring(0, 2).toUpperCase())
            }

        }
    }
}
