package srgpanov.yandex_test_task.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import srgpanov.yandex_test_task.R;

/**
 * Created by Пан on 12.04.2017.
 */

public class SettingFragmentContainer extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting,container,false);
        FragmentManager manager = getFragmentManager();
        SettingsFragment settingsFragment = new SettingsFragment();
        manager.beginTransaction().add(R.id.settings_container,settingsFragment).commit();

        return rootView;
    }
}
