package com.bortolan.iquadriv2.Activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Utils.DownloadArticle;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);


        setCollapsingToolbarLayoutTitle(getIntent().getStringExtra("title"));

        new DownloadArticle(article -> {
            Picasso.with(this).load(article.getImage()).resize(600, 0).onlyScaleDown().into(image);
            content.setText(article.getBody());
        }).execute(getIntent().getStringExtra("url"));

    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        mCollapsingToolbarLayout.setTitle(title);
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
    }
}
