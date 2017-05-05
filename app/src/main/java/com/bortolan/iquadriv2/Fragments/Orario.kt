package com.bortolan.iquadriv2.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bortolan.iquadriv2.Adapters.AdapterOrari
import com.bortolan.iquadriv2.Databases.FavouritesDB
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubResponse
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Tasks.CacheObjectObservable
import com.bortolan.iquadriv2.Tasks.CacheObjectTask
import com.bortolan.iquadriv2.Utils.DownloadSchedules
import com.bortolan.iquadriv2.Utils.Methods
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_orario.*
import java.io.File

class Orario : Fragment(), AdapterOrari.UpdateFragment, SearchView.OnQueryTextListener {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_orario, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_view.setOnQueryTextListener(this)
        search_view.queryHint = "Cerca..."
        search_view.setIconifiedByDefault(false)

        load()
        if (Methods.isNetworkAvailable(context)) {
            DownloadSchedules { response -> addAll(response, true) }.execute()
        }
    }

    override fun onResume() {
        super.onResume()

        preferiti.setData("preferiti", FavouritesDB.getInstance(context).all, FavouritesDB.getInstance(context), this, false)
    }

    fun addAll(response: GitHubResponse, doCache: Boolean) {
        classi?.setData("classi", response.classi, FavouritesDB.getInstance(context), this, true)
        prof?.setData("prof", response.prof, FavouritesDB.getInstance(context), this, true)
        aule?.setData("aule", response.aule, FavouritesDB.getInstance(context), this, true)

        if (doCache) {
            CacheObjectTask(context?.cacheDir, TAG).execute(response)
        }
    }

    fun load() {
        CacheObjectObservable(File(context.cacheDir, TAG)).getCachedObject(GitHubResponse::class.java)
                .subscribeOn(Schedulers.io())
                .subscribe({ t ->
                    classi.setData("classi", t.classi, FavouritesDB.getInstance(context), this, true)
                    prof.setData("prof", t.prof, FavouritesDB.getInstance(context), this, true)
                    aule.setData("aule", t.aule, FavouritesDB.getInstance(context), this, true)
                }, { it.printStackTrace() })
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
