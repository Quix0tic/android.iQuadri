package com.bortolan.iquadriv2.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bortolan.iquadriv2.Activities.OrarioActivity;
import com.bortolan.iquadriv2.Activities.SettingsActivity;
import com.bortolan.iquadriv2.Databases.FavouritesDB;
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem;
import com.bortolan.iquadriv2.R;
import com.vansuita.gaussianblur.GaussianBlur;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Home extends Fragment {
    Context mContext;

    @BindView(R.id.image)
    ImageView imageView;
    @BindView(R.id.settings)
    ImageView settingsView;
    @BindView(R.id.favourite)
    ImageView favourite;

    FavouritesDB db;
    List<GitHubItem> itemList;

    public Home() {
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getContext();
        db = new FavouritesDB(mContext);
        ButterKnife.bind(this, layout);

        Bitmap bitmap = getBitmapFromAsset(mContext, "bg.jpeg");

        Bitmap catBitmap = GaussianBlur.with(mContext).radius(15).noScaleDown(false).render(bitmap);
        imageView.setImageBitmap(catBitmap);
        settingsView.setOnClickListener(view -> startActivity(new Intent(mContext, SettingsActivity.class)));

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        itemList = db.getAll();
        if (!itemList.isEmpty()) {
            favourite.setVisibility(View.VISIBLE);
            favourite.setOnClickListener(view -> startActivity(new Intent(mContext, OrarioActivity.class).putExtra("name", itemList.get(0).getName()).putExtra("url", itemList.get(0).getUrl())));
        } else {
            favourite.setVisibility(View.GONE);
        }
    }
}
