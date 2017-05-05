package com.bortolan.iquadriv2.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bortolan.iquadriv2.Adapters.AdapterOrari;
import com.bortolan.iquadriv2.Databases.FavouritesDB;
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem;
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubResponse;
import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Tasks.CacheObjectObservable;
import com.bortolan.iquadriv2.Tasks.CacheObjectTask;
import com.bortolan.iquadriv2.Utils.DownloadSchedules;
import com.bortolan.iquadriv2.Views.ScheduleList;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;

import static com.bortolan.iquadriv2.Utils.Methods.isNetworkAvailable;

public class Orario extends Fragment implements AdapterOrari.UpdateFragment, SearchView.OnQueryTextListener {
    private final static String TAG = Orario.class.getSimpleName();
    Context mContext;
    @BindView(R.id.search_card)
    CardView searchCard;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.preferiti)
    ScheduleList preferiti;
    @BindView(R.id.classi)
    ScheduleList classi;
    @BindView(R.id.prof)
    ScheduleList prof;
    @BindView(R.id.aule)
    ScheduleList aule;

    FavouritesDB db;

    public Orario() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_orario, container, false);
        ButterKnife.bind(this, layout);
        mContext = getContext();
        db = FavouritesDB.getInstance(mContext);

        searchView.setOnQueryTextListener(this);

        bindSchedulesCache();
        if (isNetworkAvailable(mContext)) {
            new DownloadSchedules(response -> addAll(response, true)).execute();
        }
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        preferiti.setData("preferiti", db.getAll(), db, this, false);
    }

    public void addAll(GitHubResponse response, boolean docache) {
        if (response != null) {
            classi.setData("classi", response.getClassi(), db, this, true);
            prof.setData("prof", response.getProf(), db, this, true);
            aule.setData("aule", response.getAule(), db, this, true);

            if (docache) {
                new CacheObjectTask(mContext.getCacheDir(), TAG).execute(response);
            }
        }
    }

    public void bindSchedulesCache() {
        new CacheObjectObservable(new File(mContext.getCacheDir(), TAG)).getCachedObject(GitHubResponse.class)
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    classi.setData("classi", response.getClassi(), db, this, true);
                    prof.setData("prof", response.getProf(), db, this, true);
                    aule.setData("aule", response.getAule(), db, this, true);
                }, Throwable::printStackTrace);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        preferiti.filter(newText);
        classi.filter(newText);
        prof.filter(newText);
        aule.filter(newText);
        return true;
    }

    @Override
    public void add(GitHubItem item, int position) {
        preferiti.add(item, position);
    }

    @Override
    public void remove(GitHubItem item, int position) {
        preferiti.remove(position);
    }

    @Override
    public void onStop() {
        super.onStop();
        FavouritesDB.getInstance(getContext()).close();
    }
}
