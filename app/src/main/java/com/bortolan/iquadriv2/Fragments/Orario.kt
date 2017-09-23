package com.bortolan.iquadriv2.Fragments


import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bortolan.iquadriv2.Adapters.AdapterOrari
import com.bortolan.iquadriv2.Databases.FavouritesDB
import com.bortolan.iquadriv2.Databases.RegistroDB
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubResponse
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Tasks.Remote.DownloadSchedules
import com.bortolan.iquadriv2.Utils.Methods
import com.bortolan.iquadriv2.Widget.WidgetOrario
import kotlinx.android.synthetic.main.fragment_orario.*


class Orario : Fragment(), AdapterOrari.UpdateFragment, SearchView.OnQueryTextListener {
    var active = false
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_orario, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_view.setOnQueryTextListener(this)
        search_view.findViewById<ImageView>(R.id.search_close_btn).setOnClickListener {
            search_view.clearFocus()
            preferiti.requestFocus()
            search_view.setQuery(null, true)
            preferiti.filter(null)
        }
    }

    override fun onResume() {
        super.onResume()

        if (!search_view.query.isNotEmpty()) {
            preferiti.setData("preferiti", FavouritesDB.getInstance(context).all, this, false)
            load()
            download()
        }

        active = true
    }

    override fun onStop() {
        super.onStop()
        active = false
        context.sendBroadcast(Intent(context, WidgetOrario::class.java).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidgetOrario::class.java))))
    }

    fun addAll(response: GitHubResponse) {
        classi?.setData("classi", response.classi, this, true)
        prof?.setData("professori", response.prof, this, true)
        aule?.setData("aule", response.aule, this, true)
    }

    fun download() {
        val mContext: Context = context
        if (Methods.isNetworkAvailable(mContext)) {
            DownloadSchedules { response: GitHubResponse ->
                RegistroDB.getInstance(mContext).addSchedules(response)
                if (active) addAll(RegistroDB.getInstance(mContext).schedules)
            }.execute()
        }
    }

    fun load() {
        addAll(RegistroDB.getInstance(context).schedules)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        search_view.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        preferiti.filter(newText)
        classi.filter(newText)
        prof.filter(newText)
        aule.filter(newText)
        return true
    }

    override fun add(item: GitHubItem, position: Int) {
        preferiti.add(item)
    }

    override fun remove(item: GitHubItem, position: Int) {
        preferiti.remove(position)
    }

    companion object {
        private val TAG = "Orario"
    }
}
