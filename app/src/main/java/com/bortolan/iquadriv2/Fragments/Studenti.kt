package com.bortolan.iquadriv2.Fragments


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bortolan.iquadriv2.Adapters.AdapterCircolari
import com.bortolan.iquadriv2.Interfaces.Circolare
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Tasks.CacheListObservable
import com.bortolan.iquadriv2.Tasks.CacheListTask
import com.bortolan.iquadriv2.Utils.DownloadRSSFeed
import com.bortolan.iquadriv2.Utils.Methods.isNetworkAvailable
import com.crazyhitty.chdev.ks.rssmanager.OnRssLoadListener
import com.crazyhitty.chdev.ks.rssmanager.RssItem
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_circolari.*
import java.io.File

/**
 * http://studenti.liceoquadri.it/feed/
 */
class Studenti : Fragment(), SwipeRefreshLayout.OnRefreshListener, OnRssLoadListener {
    internal lateinit var adapter: AdapterCircolari
    private var active = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_circolari, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AdapterCircolari(context, AdapterCircolari.MODE_QDS)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).color(Color.parseColor("#BDBDBD")).size(1).build())
        recycler.adapter = adapter

        swipe_refresh.setColorSchemeResources(R.color.bluematerial, R.color.greenmaterial, R.color.lightgreenmaterial, R.color.orangematerial, R.color.redmaterial)
        swipe_refresh.setOnRefreshListener(this)
        swipe_refresh.isRefreshing = true
        onRefresh()
    }

    override fun onResume() {
        super.onResume()
        active = true
        load()
    }

    override fun onStop() {
        super.onStop()
        active = false
    }

    override fun onRefresh() {
        if (isNetworkAvailable(context)) {
            download()
        } else {
            swipe_refresh.isRefreshing = false
        }
    }

    private fun download() {
        val mContext: Context = context
        DownloadRSSFeed("Studenti", PreferenceManager.getDefaultSharedPreferences(context)) { list: List<Circolare> ->
            if (active) {
                swipe_refresh.isRefreshing = false
                addAnnouncements(list, true)
            }
            if (!list.isEmpty())
                PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("last_studenti", list[0].title.toLowerCase().trim { it <= ' ' }).apply()
        }.execute(DownloadRSSFeed.STUDENTI)

    }

    override fun onSuccess(rssItems: List<RssItem>) {
        val list = rssItems.map { item -> Circolare(item.title.trim { it <= ' ' }, item.link.trim { it <= ' ' }, item.description.trim { it <= ' ' }) }
        addAnnouncements(list, true)
        swipe_refresh!!.isRefreshing = false
    }

    override fun onFailure(message: String) {
        Log.e(TAG, message)
        swipe_refresh!!.isRefreshing = false
    }

    internal fun addAnnouncements(announcements: List<Circolare>, docache: Boolean) {
        if (!announcements.isEmpty()) {
            adapter.clear()
            adapter.addAll(announcements)

            if (docache) {
                CacheListTask(context?.cacheDir, TAG).execute(announcements as List<*>)
            }
        }
    }

    private fun load() {
        CacheListObservable(File(context.cacheDir, TAG))
                .getCachedList(Circolare::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list -> addAnnouncements(list, false) }
    }

    companion object {
        internal val TAG = "Studenti"
    }
}
