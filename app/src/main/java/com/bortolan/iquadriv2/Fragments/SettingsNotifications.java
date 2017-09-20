package com.bortolan.iquadriv2.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.XpPreferenceFragment;

import com.bortolan.iquadriv2.R;

import net.xpece.android.support.preference.CheckBoxPreference;
import net.xpece.android.support.preference.ListPreference;
import net.xpece.android.support.preference.SwitchPreference;

public class SettingsNotifications extends XpPreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsNotifications.class.getSimpleName();

    SharedPreferences preferences;

    @Override
    public void onCreatePreferences2(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        onSharedPreferenceChanged(preferences, "notify");
        onSharedPreferenceChanged(preferences, "notify_vibrate");
        onSharedPreferenceChanged(preferences, "notify_sound");

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
        } else if (preference != null) {
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
