package com.bortolan.iquadriv2.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Tasks.Remote.DownloadArticle;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;

import static com.bortolan.iquadriv2.Utils.Methods.getDisplaySize;

public class ActivityArticle extends AppCompatActivity {

    @BindView(R.id.image)
    PhotoView image;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.shadow)
    View shadow;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    boolean wasPaused = false;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode().excludeTarget(R.id.app_bar, true).excludeTarget(android.R.id.navigationBarBackground, true).setDuration(200).setInterpolator(new DecelerateInterpolator(1.5f)));
            getWindow().setEnterTransition(new Explode().excludeTarget(R.id.app_bar, true).excludeTarget(android.R.id.navigationBarBackground, true).setDuration(200).setInterpolator(new DecelerateInterpolator(1.5f)));
        }

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
        image.setZoomable(false);
        image.getAttacher().setScaleType(ImageView.ScaleType.FIT_CENTER);
        Log.d("ARTICLE", "ENTER");
        progressBar.setVisibility(View.VISIBLE);
        new DownloadArticle(article -> {
            if (article != null) {
                runOnUiThread(() -> {
                    content.setText(article.getBody());
                    TransitionManager.beginDelayedTransition((ViewGroup) content.getRootView(), new Fade());
                });

                Picasso.with(this).load(article.getImage()).resize(getDisplaySize(this).x, 0).onlyScaleDown().into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);

                        shadow.setOnClickListener(view -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                startActivity(new Intent(ActivityArticle.this, ActivityImage.class).putExtra("url", article.getImage()), ActivityOptionsCompat.makeSceneTransitionAnimation(ActivityArticle.this, view, "image").toBundle());
                            } else {
                                startActivity(new Intent(ActivityArticle.this, ActivityImage.class));
                            }
                        });
                    }

                    @Override
                    public void onError() {
                    }
                });
            } else {
                finish();
                Toast.makeText(this, getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            }
            return Unit.INSTANCE;
        }).execute(url);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasPaused) {

        }
        wasPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasPaused = true;
    }

    private void scheduleStartPostponedTransition(View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                });
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
