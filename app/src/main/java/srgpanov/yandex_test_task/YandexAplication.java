package srgpanov.yandex_test_task;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.yandex.speechkit.SpeechKit;

import static srgpanov.yandex_test_task.Utils.ConstantManager.KEY_API_SPEECHKIT;

/**
 * Created by Пан on 11.04.2017.
 */

public class YandexAplication extends Application {
    static SharedPreferences mPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        SpeechKit.getInstance().configure(getApplicationContext(), KEY_API_SPEECHKIT);


    }

    static public SharedPreferences getPreferences() {
        return mPreferences;
    }


}
