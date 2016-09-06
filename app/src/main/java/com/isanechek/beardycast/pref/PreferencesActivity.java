package com.isanechek.beardycast.pref;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.isanechek.beardycast.R;
import com.isanechek.beardycast.utils.Util;

/**
 * Created by isanechek on 17.07.16.
 */

public class PreferencesActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.settings_activity;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            if (Util.isAndroid5Plus()) {
                finishAfterTransition();
            } else {
                finish();
            }
        });

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_fragment_container, new SettingsFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @SuppressLint("ValidFragment")
    private static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.general_settings);
        }
    }
}
