package com.bortolan.iquadriv2.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.bortolan.iquadriv2.Interfaces.Libri.Announcement
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Views.ViewHolder

class AdapterLibri : RecyclerView.Adapter<ViewHolder>(), Filterable {
    internal val filter: MyFilter = MyFilter()
    override fun getFilter(): Filter {
        return filter
    }

    internal var announcements: MutableList<Announcement> = ArrayList()
    internal var all: MutableList<Announcement> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_libri, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setTitle(announcements[position].title)
        holder.setPrice(announcements[position].price)
        holder.setISBN(announcements[position].isbn)
    }

    override fun getItemCount(): Int {
        return announcements.size
    }

    fun addAll(announcements: List<Announcement>) {
        this.announcements.addAll(announcements)
        all.addAll(announcements)
        notifyDataSetChanged()
    }

    fun clear() {
        announcements.clear()
        all.clear()
        notifyDataSetChanged()
    }

    fun getAt(position: Int): Announcement? {
        if (position < 0 || position > announcements.size) return null
        return announcements[position]
    }

    open inner class MyFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val result = FilterResults()
            val list: MutableList<Announcement> = ArrayList()
            if (!constraint.isNullOrBlank()) {
                all.forEach {
                    if (it.title.contains(constraint.toString(), true) || it.isbn.contains(constraint.toString(), true))
                        list.add(it)
                }
                result.count = list.size
                result.values = list
            } else {
                result.values = all
                result.count = all.size
            }
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            announcements = results?.values as MutableList<Announcement>
            notifyDataSetChanged()
        }

    }
}
