package com.bortolan.iquadriv2.Adapters

import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.bortolan.iquadriv2.Activities.ActivityOrario
import com.bortolan.iquadriv2.Databases.FavouritesDB
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Utils.Methods.capitalize
import java.util.*

class AdapterOrari(private val mContext: Context) : RecyclerView.Adapter<AdapterOrari.OrarioHolder>(), Filterable {

    private var filter: Filter = MyFilter()
    private val items: MutableList<GitHubItem> = LinkedList()
    private val all: MutableList<GitHubItem> = LinkedList()
    private var update: UpdateFragment? = null
    private val v: Vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    var query: CharSequence? = null

    fun setUpdateFragment(update: UpdateFragment) {
        this.update = update
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrarioHolder {
        return OrarioHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_orario, parent, false))
    }

    override fun onBindViewHolder(holder: OrarioHolder, position: Int) {
        val item = items[position]
        holder.title.text = capitalize(item.name)
        holder.layout.setOnClickListener { _ -> mContext.startActivity(Intent(mContext, ActivityOrario::class.java).putExtra("url", item.url).putExtra("name", item.name)) }
        holder.layout.setOnLongClickListener { _ ->
            v.vibrate(30)

            if (FavouritesDB.getInstance(mContext).isFavourite(item)) {
                update?.remove(item, FavouritesDB.getInstance(mContext).indexOf(item))
                FavouritesDB.getInstance(mContext).remove(item)
            } else {
                update?.add(item, FavouritesDB.getInstance(mContext).all.size)
                FavouritesDB.getInstance(mContext).add(item)
            }
            true
        }

    }

    override fun getItemCount(): Int = items.size
    fun totalItemCount(): Int = all.size

    fun getItem(i: Int): GitHubItem {
        return items[i]
    }

    fun addAll(items: List<GitHubItem>) {
        this.all.clear()
        this.all.addAll(items)
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun add(t: GitHubItem, position: Int) {
        all.add(totalItemCount(), t)
        items.add(itemCount, t)
        filter.filter(query)
    }

    fun remove(position: Int) {
        val item = all[position]
        all.removeAt(position)
        items.remove(item)
        filter.filter(query)
    }

    fun clear() {
        items.clear()
        all.clear()
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return filter
    }

    interface UpdateFragment {
        fun add(item: GitHubItem, position: Int)
        fun remove(item: GitHubItem, position: Int)
    }

    inner class OrarioHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById<TextView>(R.id.title)
        var layout: View = itemView.findViewById(R.id.layout)
    }

    private inner class MyFilter : Filter() {

        override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()
            if (!charSequence.isNullOrEmpty()) {
                val filtered = all.filterTo(LinkedList<GitHubItem>()) { item -> item.name.toLowerCase().trim { it <= ' ' }.contains(charSequence.toString().toLowerCase().trim { it <= ' ' }) }

                results.count = filtered.size
                results.values = filtered
            } else {
                results.count = all.size
                results.values = all
            }
            return results
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
            items.clear()
            items.addAll(filterResults.values as MutableList<GitHubItem>)
            notifyDataSetChanged()
        }
    }

    private inner class MyCallback(private val old: List<GitHubItem>, private val new: List<GitHubItem>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = old[oldItemPosition].name.equals(new[newItemPosition].name, true)
        override fun getOldListSize(): Int = old.size
        override fun getNewListSize(): Int = new.size
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = old[oldItemPosition].url.equals(new[newItemPosition].url, true)
    }
}
