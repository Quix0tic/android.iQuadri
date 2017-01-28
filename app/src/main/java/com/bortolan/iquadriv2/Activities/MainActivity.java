package com.bortolan.iquadriv2.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bortolan.iquadriv2.Broadcasts.Notifiche;
import com.bortolan.iquadriv2.Fragments.Circolari;
import com.bortolan.iquadriv2.Fragments.Home;
import com.bortolan.iquadriv2.Fragments.Login;
import com.bortolan.iquadriv2.Fragments.Orario;
import com.bortolan.iquadriv2.Fragments.Registro;
import com.bortolan.iquadriv2.Fragments.RegistroPeriodi;
import com.bortolan.iquadriv2.Fragments.Studenti;
import com.bortolan.iquadriv2.R;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.bortolan.iquadriv2.Utils.Methods.setAlarm;

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
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();

        Fabric.with(this, new Crashlytics(), new Answers());

        checkNotifications();

        navigation_bar.setOnTabSelectListener(this);
        if (savedInstanceState != null)
            try {
                navigation_bar.setDefaultTab(getIntent().getExtras().getInt("tab"));
            } catch (Exception e) {
                navigation_bar.setDefaultTab(R.id.tab_home);
            }
        else
            navigation_bar.setDefaultTab(R.id.tab_home);
    }

    private void checkNotifications() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent operation = PendingIntent.getBroadcast(this, NOTIFICATION_ID, new Intent(this, Notifiche.class), 0);


        if (preferences.getBoolean("first_run", true)) {
            preferences.edit().putBoolean("first_run", false).apply();
            if (preferences.getBoolean("notify", true)) {
                if (preferences.getBoolean("notify_circolari", true) || preferences.getBoolean("notify_studenti", true)) {
                    Log.d("NOTIFICATION", "MAIN/check - INTERVAL: " + preferences.getString("notify_frequency", String.valueOf(AlarmManager.INTERVAL_HOUR)));
                    setAlarm(alarmManager, preferences, operation);
                }
            } else {
                alarmManager.cancel(operation);
            }
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
                if (getSharedPreferences("registro", MODE_PRIVATE).getBoolean("logged", false))
                    fragment = new RegistroPeriodi();
                else
                    fragment = new Login();
                break;
            case R.id.tab_orario:
                fragment = new Orario();
                break;
        }
        if (fragment != null)
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.content, fragment).commit();

    }
}
