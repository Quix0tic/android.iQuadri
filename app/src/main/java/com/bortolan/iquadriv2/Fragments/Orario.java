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
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubResponse;
import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Tasks.CacheObjectObservable;
import com.bortolan.iquadriv2.Tasks.CacheObjectTask;
import com.bortolan.iquadriv2.Utils.DownloadSchedules;
import com.bortolan.iquadriv2.Views.ScheduleList;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.bortolan.iquadriv2.Utils.Methods.isNetworkAvailable;

public class Orario extends Fragment implements AdapterOrari.UpdateFragment {
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
        db = new FavouritesDB(mContext);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });
        bindSchedulesCache();
        if (isNetworkAvailable(mContext)) {
            new DownloadSchedules(response -> {
                if (response != null) {
                    classi.setData("classi", response.getClassi(), db, this);
                    prof.setData("prof", response.getProf(), db, this);
                    aule.setData("aule", response.getAule(), db, this);
                    new CacheObjectTask(mContext.getCacheDir(), TAG).execute(response);
                }
            }).execute();
        }
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        preferiti.setData("preferiti", db.getAll(), db, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void bindSchedulesCache() {
        new CacheObjectObservable(new File(mContext.getCacheDir(), TAG))
                .getCachedObject(GitHubResponse.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        classi.setData("classi", response.getClassi(), db, this);
                        prof.setData("prof", response.getProf(), db, this);
                        aule.setData("aule", response.getAule(), db, this);
                    }
                });
    }

    @Override
    public void update() {
        preferiti.setData("preferiti", db.getAll(), db, this);
    }
}
