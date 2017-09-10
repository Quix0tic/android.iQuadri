package com.bortolan.iquadriv2.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bortolan.iquadriv2.Activities.ActivityArticle
import com.bortolan.iquadriv2.Activities.ActivityCircolari
import com.bortolan.iquadriv2.Interfaces.Circolare
import com.bortolan.iquadriv2.R
import java.util.*


class AdapterCircolari(private val mContext: Context, private val mode: Int) : RecyclerView.Adapter<AdapterCircolari.CircolariHolder>() {
    private val data: MutableList<Circolare>

    init {
        data = ArrayList<Circolare>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircolariHolder {
        return CircolariHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_circolari, parent, false))
    }

    override fun onBindViewHolder(holder: CircolariHolder, position: Int) {
        val row: Circolare = data[position]

        holder.title.text = row.title.trim { it <= ' ' }
        holder.description.text = row.description.trim { it <= ' ' }

        holder.surface.setOnClickListener { view ->
            if (mode == MODE_QDS)
                mContext.startActivity(Intent(mContext, ActivityArticle::class.java).putExtra("url", row.link).putExtra("title", row.title))
            else {
                mContext.startActivity(Intent(mContext, ActivityCircolari::class.java))
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun addAll(list: List<Circolare>) {
        data.addAll(list)
        notifyDataSetChanged()
    }

    inner class CircolariHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById<TextView>(R.id.title)
        val description: TextView = itemView.findViewById<TextView>(R.id.description)
        val surface: View = itemView.findViewById<View>(R.id.surface)
    }

    companion object {
        val MODE_QDS = 1
        val MODE_CIRCOLARE = 0
    }
}
