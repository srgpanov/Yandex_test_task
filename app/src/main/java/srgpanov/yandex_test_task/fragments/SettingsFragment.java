package srgpanov.yandex_test_task.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;

import srgpanov.yandex_test_task.R;
import srgpanov.yandex_test_task.Utils.ConstantManager;

/**
 * Created by Пан on 28.03.2017.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences mPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        addPreferencesFromResource(R.xml.fragment_preference);
        ListPreference speechVoice = (ListPreference) findPreference("speech_voice");
        ListPreference delayTranslate = (ListPreference) findPreference("delay_to_translate");
        updateListPrefSummary();
        speechVoice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object setting) {
                String voice = "";
                switch ((String) setting) {
                    case "Захар":
                        voice = ConstantManager.SPEECH_VOICE_ZAHAR;
                        break;
                    case "Евгения":
                        voice = ConstantManager.SPEECH_VOICE_JANE;
                        break;
                    case "Алиса":
                        voice = ConstantManager.SPEECH_VOICE_ALYSS;
                        break;
                    case "Борис":
                        voice = ConstantManager.SPEECH_VOICE_ERMIL;
                        break;
                    case "Омаз":
                        voice = ConstantManager.SPEECH_VOICE_OMAZH;
                        break;
                    default:
                        voice = ConstantManager.SPEECH_VOICE_ZAHAR;
                }
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(ConstantManager.SPEECH_VOICE, voice);
                editor.apply();
                return true;
            }
        });
        delayTranslate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object setting) {
                int delay =2000;
                switch ((String)setting){
                    case "Быстрая":
                        delay = ConstantManager.DELAY_LOW;
                        break;
                    case "Средняя":
                        delay=ConstantManager.DELAY_NORMAL;
                        break;
                    case "Медленная":
                        delay = ConstantManager.DELAY_HIGHT;
                        break;
                    default: delay=2000;
                }
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt(ConstantManager.DELAY_TO_TRANSLATE, delay);
                editor.apply();
                return true;
            }
        });

    }
    private void updateListPrefSummary() {
        ListPreference preferenceVoice = (ListPreference) findPreference("speech_voice");
        if (preferenceVoice.getEntry()==null){
            preferenceVoice.setSummary(getString(R.string.curent_voice) +" "+ "Захар");
        }else
        preferenceVoice.setSummary(getString(R.string.curent_voice) +" "+ preferenceVoice.getEntry());

        ListPreference preferenceDelay = (ListPreference) findPreference("delay_to_translate");
        if(preferenceDelay.getEntry()==null){
            preferenceDelay.setSummary("Средняя"+" "+getString(R.string.curent_delay));
        }else
        preferenceDelay.setSummary(preferenceDelay.getEntry()+" "+getString(R.string.curent_delay));

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("speech_voice")||key.equals("delay_to_translate")) {
            updateListPrefSummary();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
