package com.bortolan.iquadriv2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bortolan.iquadriv2.Activities.ActivityArticle;
import com.bortolan.iquadriv2.Interfaces.Circolare;
import com.bortolan.iquadriv2.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterCircolari extends RecyclerView.Adapter<AdapterCircolari.CircolariHolder> {
    public final static int MODE_QDS = 1;
    private final static int MODE_CIRCOLARE = 0;
    private List<Circolare> data;
    private Context mContext;
    private int mode;

    public AdapterCircolari(Context c, int mode) {
        data = new ArrayList<>();
        mContext = c;
        this.mode = mode;
    }

    @Override
    public CircolariHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CircolariHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_circolari, parent, false));
    }

    @Override
    public void onBindViewHolder(CircolariHolder holder, int position) {
        Circolare row = data.get(position);

        holder.title.setText(row.getTitle().trim());
        holder.description.setText(row.getDescription().trim());

        holder.surface.setOnClickListener((view) -> {
            if (mode == MODE_CIRCOLARE)
                mContext.startActivity(new Intent(mContext, ActivityArticle.class).putExtra("url", row.getLink()).putExtra("title", row.getTitle()));
            else
                openBrowser(row.getLink());
        });

    }


    private void openBrowser(String link) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        mContext.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<? extends Circolare> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    class CircolariHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.surface)
        View surface;

        CircolariHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
