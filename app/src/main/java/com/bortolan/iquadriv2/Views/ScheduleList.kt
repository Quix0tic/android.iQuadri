package com.bortolan.iquadriv2.Views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.bortolan.iquadriv2.Adapters.AdapterOrari
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem
import com.bortolan.iquadriv2.R
import java.util.*

open class ScheduleList : LinearLayout {
    internal var updateFragment: AdapterOrari.UpdateFragment? = null
    private lateinit var adapter: AdapterOrari
    private var order: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        View.inflate(context, R.layout.view_orari, this)

        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler.setHasFixedSize(true)
        recycler.layoutManager.isAutoMeasureEnabled = true
        adapter = AdapterOrari(context)
        recycler.adapter = adapter
    }

    fun setData(title: String, items: List<GitHubItem>, updateFragment: AdapterOrari.UpdateFragment, order: Boolean) {
        this.updateFragment = updateFragment
        this.order = order
        adapter.setUpdateFragment(updateFragment)

        //set title
        if (!title.isNullOrEmpty()) this.title.text = title.toUpperCase()

        //title callback
        recycler.clearOnScrollListeners()
        recycler.addOnScrollListener(myScrollListener(this.title))

        if (items.isNotEmpty()) {
            if (order) {
                Collections.sort(items) { o1, o2 -> o1.name.compareTo(o2.name, ignoreCase = true) }
            }

            adapter.clear()
            adapter.addAll(items)

            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }

    fun remove(position: Int) {
        adapter.remove(position)
        visibility = if (adapter.totalItemCount() > 0) View.VISIBLE else View.GONE
    }

    fun add(t: GitHubItem, position: Int) {
        adapter.add(t, position)
        visibility = if (adapter.totalItemCount() > 0) View.VISIBLE else View.GONE
    }

    fun filter(s: String?) {
        adapter.filter.filter(s)
        adapter.query = s
    }


    protected inner class myScrollListener internal constructor(private val textView: TextView) : RecyclerView.OnScrollListener() {
        internal var _text: String = textView.text.toString()
        internal var myState: Int = 0
        internal var firstVisible: Int = 0

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            myState = newState
            if (myState == RecyclerView.SCROLL_STATE_IDLE) textView.text = _text
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (myState != RecyclerView.SCROLL_STATE_IDLE) {
                firstVisible = (recyclerView!!.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (firstVisible >= 0 && firstVisible < recyclerView.adapter.itemCount)
                    textView.text = String.format("%1s - %2s", _text, (recyclerView.adapter as AdapterOrari).getItem(firstVisible).name.substring(0, 2).toUpperCase())
            }

        }
    }
}
