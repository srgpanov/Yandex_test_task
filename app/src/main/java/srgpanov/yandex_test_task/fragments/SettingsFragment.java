package srgpanov.yandex_test_task.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import srgpanov.yandex_test_task.R;

/**
 * Created by Пан on 28.03.2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_preference,rootKey);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
