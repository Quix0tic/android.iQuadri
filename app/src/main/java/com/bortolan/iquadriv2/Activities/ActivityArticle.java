package com.bortolan.iquadriv2.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Utils.DownloadArticle;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bortolan.iquadriv2.Utils.Methods.getDisplaySize;

public class ActivityArticle extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        ButterKnife.bind(this);

        url = getIntent().getStringExtra("url");

        setCollapsingToolbarLayoutTitle(getIntent().getStringExtra("title"));

        new DownloadArticle(article -> {
            if (article != null) {
                Picasso.with(this).load(article.getImage()).resize(getDisplaySize(this).x, 0).onlyScaleDown().into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        image.setOnClickListener(view -> {
                            Log.d("IMAGE", "CLICK");
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });
                content.setText(article.getBody());
            } else {
                finish();
                Toast.makeText(this, getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            }
        }).execute(url);

    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        mCollapsingToolbarLayout.setTitle(title);
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) finish();

        return super.onOptionsItemSelected(item);
    }

    private void openBrowser(String link) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        startActivity(i);
    }

    public void openBrowser(MenuItem item) {
        openBrowser(getIntent().getStringExtra("url"));
    }
}
