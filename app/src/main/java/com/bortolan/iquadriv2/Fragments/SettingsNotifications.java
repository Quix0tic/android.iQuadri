package com.bortolan.iquadriv2.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.XpPreferenceFragment;

import com.bortolan.iquadriv2.BuildConfig;
import com.bortolan.iquadriv2.R;
import com.google.android.gms.ads.AdRequest;

import net.xpece.android.support.preference.CheckBoxPreference;
import net.xpece.android.support.preference.ListPreference;
import net.xpece.android.support.preference.SwitchPreference;

import static com.bortolan.iquadriv2.Utils.Methods.disableAds;

public class SettingsNotifications extends XpPreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsNotifications.class.getSimpleName();

    SharedPreferences preferences;
    AdRequest.Builder myReq;

    @Override
    public void onCreatePreferences2(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        onSharedPreferenceChanged(preferences, "notify");
        onSharedPreferenceChanged(preferences, "notify_vibrate");
        onSharedPreferenceChanged(preferences, "notify_sound");

        myReq = new AdRequest.Builder();
        if (BuildConfig.DEBUG) myReq.addTestDevice("66FFAE1B2C386120B0D503E13F65ED71");
        findPreference("disattiva_pubblicita").setOnPreferenceClickListener(preference1 -> {
            disableAds(getActivity(), myReq.build());
            return true;
        });

        findPreference("disattiva_pubblicita").setEnabled(PreferenceManager.getDefaultSharedPreferences(getContext()).getLong("next_interstitial_date", 0L) <= System.currentTimeMillis());
    }

    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference == null) return;

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (preference instanceof CheckBoxPreference || preference instanceof SwitchPreference) {
            if (!key.equals("notify_circolari") && !key.equals("notify_studenti") && !key.equals("studenti") && !key.equals("genitori") && !key.equals("docenti") && !key.equals("ata")) {
                preference.setSummary(sharedPreferences.getBoolean(key, true) ? "On" : "Off");
            }
        } else if (!key.equals("disattiva_pubblicita")) {
            preference.setSummary(sharedPreferences.getString(key, ""));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
