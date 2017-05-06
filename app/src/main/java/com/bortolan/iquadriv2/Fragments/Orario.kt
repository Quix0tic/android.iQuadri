package com.bortolan.iquadriv2.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bortolan.iquadriv2.Adapters.AdapterOrari
import com.bortolan.iquadriv2.Databases.FavouritesDB
import com.bortolan.iquadriv2.Databases.RegistroDB
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubResponse
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Utils.DownloadSchedules
import com.bortolan.iquadriv2.Utils.Methods
import kotlinx.android.synthetic.main.fragment_orario.*

class Orario : Fragment(), AdapterOrari.UpdateFragment, SearchView.OnQueryTextListener {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_orario, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_view.setOnQueryTextListener(this)
        search_view.findViewById(R.id.search_close_btn).setOnClickListener {
            search_view.clearFocus()
            preferiti.requestFocus()
            search_view.setQuery("", true)
        }
    }

    override fun onResume() {
        super.onResume()

        preferiti.setData("preferiti", FavouritesDB.getInstance(context).all, FavouritesDB.getInstance(context), this, false)
        load()
        download()
    }

    fun addAll(response: GitHubResponse) {
        classi?.setData("classi", response.classi, FavouritesDB.getInstance(context), this, true)
        prof?.setData("professori", response.prof, FavouritesDB.getInstance(context), this, true)
        aule?.setData("aule", response.aule, FavouritesDB.getInstance(context), this, true)

    }

    fun download() {
        if (Methods.isNetworkAvailable(context)) {
            DownloadSchedules { response ->
                save(response)
                load()
            }.execute()
        }
    }

    fun load() {
        addAll(RegistroDB.getInstance(context).schedules)
    }

    fun save(response: GitHubResponse) {
        RegistroDB.getInstance(context).addSchedules(response)
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
        preferiti.add(item, position)
    }

    override fun remove(item: GitHubItem, position: Int) {
        preferiti.remove(position)
    }

    companion object {
        private val TAG = "Orario"
    }
}
