package com.bortolan.iquadriv2.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bortolan.iquadriv2.API.SpiaggiariApiClient;
import com.bortolan.iquadriv2.Interfaces.MarkSubject;
import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Tasks.CacheListObservable;
import com.bortolan.iquadriv2.Tasks.CacheListTask;

import java.io.File;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.bortolan.iquadriv2.Utils.Methods.isNetworkAvailable;

public class RegistroPeriodi extends Fragment {

    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    TabLayout tabLayout;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    Context mContext;

    PeriodiPager periodiAdapter;

    public RegistroPeriodi() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_registro_periodi, container, false);
        ButterKnife.bind(this, layout);
        mContext = getContext();

        periodiAdapter = new PeriodiPager(getChildFragmentManager());
        viewPager.setAdapter(periodiAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        //update data
        bindMarksSubjectsCache();
        UpdateMedie();

        return layout;
    }


    private void UpdateMedie() {
        if (isNetworkAvailable(mContext)) {
            new SpiaggiariApiClient(mContext).mService.getMarks()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(marks -> addSubjects(marks, true), throwable -> {
                    });
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
        }
    }

    private void addSubjects(List<MarkSubject> markSubjects, boolean docache) {
        if (!markSubjects.isEmpty()) {
            Registro fragment;
            for (int i = 0; i < periodiAdapter.getCount(); i++) {
                fragment = (Registro) periodiAdapter.instantiateItem(viewPager, i);
                fragment.addSubjects(markSubjects);
            }

            if (docache) {
                // Update cache
                new CacheListTask(mContext.getCacheDir(), Registro.TAG).execute((List) markSubjects);
            }
        }
    }

    private void bindMarksSubjectsCache() {
        new CacheListObservable(new File(mContext.getCacheDir(), Registro.TAG))
                .getCachedList(MarkSubject.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(marksSubjects -> {
                    addSubjects(marksSubjects, false);
                    Log.d(Registro.TAG, "Restored cache");
                });
    }

    class PeriodiPager extends FragmentPagerAdapter {

        PeriodiPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Registro fragment = new Registro();
            Bundle bundle = new Bundle();
            bundle.putInt("q", position);
            fragment.setArguments(bundle);
            return fragment;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 2) return "Generale";
            return String.format(Locale.getDefault(), "%d° Periodo", position + 1);
        }


        @Override
        public int getCount() {
            return 3;
        }
    }

}
