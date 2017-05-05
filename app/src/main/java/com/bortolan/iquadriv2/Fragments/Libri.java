package com.bortolan.iquadriv2.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bortolan.iquadriv2.Adapters.AdapterLibri;
import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Views.SwipableRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bortolan.iquadriv2.Utils.Methods.dpToPx;

public class Libri extends Fragment {
    @BindView(R.id.recycler)
    SwipableRecyclerView recyclerView;
    AdapterLibri adapter;

    public Libri() {
        // Required empty public constructor
    }

    public static Libri newInstance() {
        return new Libri();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_libri, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        adapter = new AdapterLibri();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).size((int) dpToPx(1)).build());
    }
}
