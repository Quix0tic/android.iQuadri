package com.bortolan.iquadriv2.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bortolan.iquadriv2.Adapters.AdapterCircolari;
import com.bortolan.iquadriv2.Interfaces.Circolare;
import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Tasks.CacheListObservable;
import com.bortolan.iquadriv2.Tasks.CacheListTask;
import com.bortolan.iquadriv2.Utils.DownloadRSSFeed;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.bortolan.iquadriv2.Utils.DownloadRSSFeed.CIRCOLARI;
import static com.bortolan.iquadriv2.Utils.Methods.isNetworkAvailable;

public class Circolari extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    static final String TAG = Circolari.class.getSimpleName();

    Context mContext;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    AdapterCircolari adapter;

    public Circolari() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_circolari, container, false);
        ButterKnife.bind(this, layout);

        mContext = getContext();

        adapter = new AdapterCircolari(mContext, true);
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
        new DownloadRSSFeed("Circolari", PreferenceManager.getDefaultSharedPreferences(mContext), list -> {
            swipeRefreshLayout.setRefreshing(false);
            addAnnouncements(list, true);
            if (!list.isEmpty())
                PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("last_circolare", list.get(0).getTitle().toLowerCase().trim()).apply();
        }).execute(CIRCOLARI);
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

