package com.bortolan.iquadriv2.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Views.ViewHolder;

public class AdapterLibri extends RecyclerView.Adapter<ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libri, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setTitle("Geografia e Preistoria");
        holder.setPrice(15);
        holder.setISBN(4984311915674L + position + "");
    }

    @Override
    public int getItemCount() {
        return 50;
    }
}
