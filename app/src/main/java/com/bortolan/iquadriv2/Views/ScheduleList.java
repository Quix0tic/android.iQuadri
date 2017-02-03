package com.bortolan.iquadriv2.Views;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bortolan.iquadriv2.Adapters.AdapterOrari;
import com.bortolan.iquadriv2.Databases.FavouritesDB;
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem;
import com.bortolan.iquadriv2.R;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleList extends LinearLayout {
    private Context mContext;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private AdapterOrari adapter;
    private List<GitHubItem> items;
    FavouritesDB db;
    AdapterOrari.UpdateFragment updateFragment;

    public ScheduleList(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ScheduleList(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ScheduleList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    public void init() {
        inflate(mContext, R.layout.view_orari, this);
        ButterKnife.bind(this);

        items = new LinkedList<>();

        recycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recycler.setHasFixedSize(true);
        recycler.getLayoutManager().setAutoMeasureEnabled(true);
    }

    public void setData(String title, List<GitHubItem> items, FavouritesDB db, AdapterOrari.UpdateFragment updateFragment) {
        this.db = db;
        this.updateFragment = updateFragment;

        if (adapter != null) adapter.clear();
        this.items.clear();
        if (items != null && items.size() != 0) {
            this.title.setText(title.toUpperCase());
            this.items.addAll(items);

            adapter = new AdapterOrari(mContext, db, updateFragment);
            adapter.addAll(items);
            recycler.setAdapter(adapter);
            recycler.addOnScrollListener(new myScrollListener(this.title));

            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    public void filter(String s) {
        if (adapter != null)
            adapter.getFilter().filter(s);
    }


    protected class myScrollListener extends RecyclerView.OnScrollListener {
        private TextView textView;
        String _text;
        int myState, firstVisible;

        public myScrollListener(TextView title) {
            super();
            textView = title;
            _text = title.getText().toString();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            myState = newState;
            if (myState == RecyclerView.SCROLL_STATE_IDLE) textView.setText(_text);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (myState != RecyclerView.SCROLL_STATE_IDLE) {
                firstVisible = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                //QUANDO FALLISCE IL DOWNLOAD NON CI SONO ITEM -> IndexOutOfBound
                if (((AdapterOrari) recyclerView.getAdapter()).getItem(firstVisible) != null)
                    textView.setText(_text + " - " + ((AdapterOrari) recyclerView.getAdapter()).getItem(firstVisible).getName().substring(0, 2).toUpperCase());
            }

        }
    }
}
