package com.bortolan.iquadriv2.Activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.bortolan.iquadriv2.BuildConfig;
import com.bortolan.iquadriv2.Databases.FavouritesDB;
import com.bortolan.iquadriv2.Databases.RegistroDB;
import com.bortolan.iquadriv2.Fragments.Circolari;
import com.bortolan.iquadriv2.Fragments.Home;
import com.bortolan.iquadriv2.Fragments.Libri;
import com.bortolan.iquadriv2.Fragments.Login;
import com.bortolan.iquadriv2.Fragments.Orario;
import com.bortolan.iquadriv2.Fragments.RegistroPeriodi;
import com.bortolan.iquadriv2.Fragments.Studenti;
import com.bortolan.iquadriv2.R;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.firebase.messaging.FirebaseMessaging;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements OnTabSelectListener {
    public final static int NOTIFICATION_ID = 447124;
    public static final int CIRCOLARI_ID = 446123;
    public static final int STUDENTI_ID = 561231;

    @BindView(R.id.navigation_bar)
    BottomBar navigation_bar;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        if (!BuildConfig.DEBUG) {
            final Fabric fabric = new Fabric.Builder(this)
                    .kits(new Crashlytics(), new Answers())
                    .build();
            Fabric.with(fabric);
        }

        FirebaseMessaging.getInstance().unsubscribeFromTopic("android_" + (BuildConfig.VERSION_CODE - 1));
        FirebaseMessaging.getInstance().subscribeToTopic("android_" + (BuildConfig.VERSION_CODE));

        navigation_bar.setOnTabSelectListener(this);
        navigation_bar.setDefaultTab(getIntent().getIntExtra("tab", R.id.tab_home));
    }


    @Override
    public void onTabSelected(@IdRes int tabId) {
        Fragment fragment = null;
        switch (tabId) {
            case R.id.tab_home:
                fragment = new Home();
                break;
            case R.id.tab_circolari:
                fragment = new Circolari();
                break;
            case R.id.tab_avvisi:
                fragment = new Studenti();
                break;
            case R.id.tab_registro:
                if (getSharedPreferences("registro", MODE_PRIVATE).getBoolean("logged", false))
                    fragment = new RegistroPeriodi();
                else
                    fragment = new Login();
                break;
            case R.id.tab_orario:
                fragment = new Orario();
                break;
            case R.id.tab_libri:
                fragment = new Libri();
                Bundle b = new Bundle();
                b.putString("query", getIntent().getStringExtra("query"));
                fragment.setArguments(b);
                break;
        }
        if (fragment != null)
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.content, fragment).commit();

    }

    @Override
    protected void onStop() {
        super.onStop();
        FavouritesDB.getInstance(this).close();
        RegistroDB.getInstance(this).close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
