package com.bortolan.iquadriv2.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bortolan.iquadriv2.Adapters.AdapterMedie;
import com.bortolan.iquadriv2.Interfaces.Average;
import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Utils.ItemOffsetDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class Registro extends Fragment {
    final static String TAG = Registro.class.getSimpleName();
    Context mContext;
    AdapterMedie adapter;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.logout)
    View logoutView;

    int periodo;

    public Registro() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_registro, container, false);
        ButterKnife.bind(this, layout);

        mContext = getActivity();
        periodo = getArguments().getInt("q");
        logoutView.setOnClickListener(this::logout);

        adapter = new AdapterMedie(mContext, periodo);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.addItemDecoration(new ItemOffsetDecoration(mContext, R.dimen.card_margin));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        return layout;
    }

    public void logout(View v) {
        SharedPreferences settings = mContext.getSharedPreferences("registro", MODE_PRIVATE);
        settings.edit().putBoolean("logged", false).apply();

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.content, new Login())
                .commit();
    }


    public void addSubjects(List<Average> markSubjects) {
        if (!markSubjects.isEmpty() && adapter != null) {
            adapter.clear();
            adapter.addAll(markSubjects);
        }
    }
}
