package com.bortolan.iquadriv2.Activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.messaging.FirebaseMessaging;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.bortolan.iquadriv2.Utils.Methods.disableAds;

public class ActivityMain extends AppCompatActivity implements OnTabSelectListener {
    public final static int NOTIFICATION_ID = 447124;
    public static final int CIRCOLARI_ID = 446123;
    public static final int STUDENTI_ID = 561231;

    @BindView(R.id.navigation_bar)
    BottomBar navigation_bar;

    FragmentManager fragmentManager;
    InterstitialAd interstitialAd;
    Boolean showAd = false;
    AdRequest myRequest;

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
            myRequest = new AdRequest.Builder().build();
        } else {
            myRequest = new AdRequest.Builder().addTestDevice("66FFAE1B2C386120B0D503E13F65ED71").build();
        }

        prepareInterstitial();

        ifHuaweiAlert();

        //FirebaseMessaging.getInstance().unsubscribeFromTopic("android_14");
        FirebaseMessaging.getInstance().subscribeToTopic("android_14");

        navigation_bar.setOnTabSelectListener(this);
        navigation_bar.setDefaultTab(getIntent().getIntExtra("tab", R.id.tab_home));
    }

    private void prepareInterstitial() {
        if (PreferenceManager.getDefaultSharedPreferences(ActivityMain.this).getLong("next_interstitial_date", 0L) < System.currentTimeMillis()) {
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId("ca-app-pub-6428554832398906/7348876488");
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    showAd = true;
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    showAd = false;
                    //PreferenceManager.getDefaultSharedPreferences(ActivityMain.this).edit().putLong("next_interstitial_date", System.currentTimeMillis() + 2 * 60 * 60 * 1000L).apply();
                }
            });
            interstitialAd.loadAd(myRequest);
        }

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_v17", true)) {
            new MaterialDialog.Builder(this)
                    .title("Vuoi tenere le pubblicità?")
                    .content("Con l'ultimo aggiornamento, per supportare i costi dello sviluppo e del mantenimento dell'app, sono state implementate le pubblicità a schermo intero in chiusura all'app.\nSe ciò ti da' fastidio, puoi sempre disattivarle per 2 settimane guardando un breve video.")
                    .positiveText("SI").negativeText("NO")
                    .onNegative((dialog, which) -> disableAds(this, myRequest))
                    .show();

            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_v17", false).apply();
        }
    }

    private void ifHuaweiAlert() {
        if ("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER) && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("protected", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.huawei_headline).setMessage(R.string.huawei_text)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                        startActivity(intent);
                        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("protected", true).apply();
                    }).setNegativeButton(android.R.string.cancel, null).create().show();
        }
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
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("spaggiari-logged", false))
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
    public void onBackPressed() {
        if (showAd)
            interstitialAd.show();
        else
            super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FavouritesDB.getInstance(this).close();
        RegistroDB.getInstance(this).close();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
