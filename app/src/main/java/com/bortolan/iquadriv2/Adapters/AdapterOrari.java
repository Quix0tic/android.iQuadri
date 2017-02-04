package com.bortolan.iquadriv2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bortolan.iquadriv2.Activities.OrarioActivity;
import com.bortolan.iquadriv2.Databases.FavouritesDB;
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem;
import com.bortolan.iquadriv2.R;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bortolan.iquadriv2.Utils.Methods.capitalize;

public class AdapterOrari extends RecyclerView.Adapter<AdapterOrari.OrarioHolder> implements Filterable {
    private Context mContext;

    private Filter filter;
    private List<GitHubItem> items, all;
    private FavouritesDB db;
    private UpdateFragment update;
    private Vibrator v;

    public AdapterOrari(Context mContext, FavouritesDB db, UpdateFragment update) {
        this.mContext = mContext;
        this.db = db;
        this.update = update;

        v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        items = new LinkedList<>();
        all = new LinkedList<>();
    }

    @Override
    public OrarioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrarioHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_orario, parent, false));
    }

    @Override
    public void onBindViewHolder(OrarioHolder holder, int position) {
        GitHubItem item = items.get(position);
        holder.title.setText(capitalize(item.getName()));
        holder.layout.setOnClickListener(view -> mContext.startActivity(new Intent(mContext, OrarioActivity.class).putExtra("url", item.getUrl()).putExtra("name", item.getName())));
        holder.layout.setOnLongClickListener(view -> {
            v.vibrate(30);

            if (db.isFavourite(item)) {
                db.remove(item);
            } else {
                db.add(item);
            }
            update.update();
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public GitHubItem getItem(int i) {
        return items.get(i);
    }

    public void addAll(List<GitHubItem> items) {
        this.items = new LinkedList<>(items);
        this.all = new LinkedList<>(items);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        all.clear();
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new MyFilter();
        return filter;
    }

    public interface UpdateFragment {
        void update();
    }

    class OrarioHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.layout)
        View layout;

        OrarioHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if (charSequence != null && charSequence.length() > 0) {
                List<GitHubItem> filtered = new LinkedList<>();

                for (GitHubItem item : all) {
                    if (item.getName().toLowerCase().trim().contains(charSequence.toString().toLowerCase().trim())) {
                        filtered.add(item);
                    }
                }
                results.count = filtered.size();
                results.values = filtered;
            } else {
                results.count = all.size();
                results.values = all;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            items = (List<GitHubItem>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
