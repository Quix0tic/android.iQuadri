package com.bortolan.iquadriv2.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bortolan.iquadriv2.Adapters.AdapterCircolari;
import com.bortolan.iquadriv2.Interfaces.Circolare;
import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Tasks.CacheListObservable;
import com.bortolan.iquadriv2.Tasks.CacheListTask;
import com.bortolan.iquadriv2.Utils.DownloadRSSFeed;
import com.crazyhitty.chdev.ks.rssmanager.OnRssLoadListener;
import com.crazyhitty.chdev.ks.rssmanager.RssItem;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.bortolan.iquadriv2.Utils.DownloadRSSFeed.STUDENTI;
import static com.bortolan.iquadriv2.Utils.Methods.isNetworkAvailable;

/**
 * http://studenti.liceoquadri.it/feed/
 */
public class Studenti extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnRssLoadListener {
    static final String TAG = Studenti.class.getSimpleName();


    Context mContext;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    AdapterCircolari adapter;

    public Studenti() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_circolari, container, false);
        ButterKnife.bind(this, layout);
        mContext = getContext();

        adapter = new AdapterCircolari(mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).color(Color.parseColor("#BDBDBD")).size(1).build());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.bluematerial, R.color.greenmaterial, R.color.lightgreenmaterial, R.color.orangematerial, R.color.redmaterial);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindAnnouncementsCache();
    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable(mContext)) {
            loadFeeds();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    //load feeds
    private void loadFeeds() {
        //you can also pass multiple urls

        new DownloadRSSFeed("Studenti", PreferenceManager.getDefaultSharedPreferences(mContext), list -> {
            addAnnouncements(list, true);
            swipeRefreshLayout.setRefreshing(false);
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("last_studenti", list.get(0).getTitle().toLowerCase().trim()).apply();
        }).execute(STUDENTI);

    }

    @Override
    public void onSuccess(List<RssItem> rssItems) {
        List<Circolare> list = new ArrayList<>();
        for (RssItem item : rssItems) {

            list.add(new Circolare(item.getTitle().trim(), item.getLink().trim(), item.getDescription().trim()));
        }
        addAnnouncements(list, true);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(String message) {
        Log.e(TAG, message);
        swipeRefreshLayout.setRefreshing(false);
    }

    void addAnnouncements(List<Circolare> announcements, boolean docache) {
        if (!announcements.isEmpty()) {
            adapter.clear();
            adapter.addAll(announcements);

            if (docache) {
                new CacheListTask(mContext.getCacheDir(), TAG).execute((List) announcements);
            }
        }
    }

    private void bindAnnouncementsCache() {
        new CacheListObservable(new File(mContext.getCacheDir(), TAG))
                .getCachedList(Circolare.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    addAnnouncements(list, false);
                });
    }
}
