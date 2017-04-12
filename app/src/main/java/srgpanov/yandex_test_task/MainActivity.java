package srgpanov.yandex_test_task;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import srgpanov.yandex_test_task.Utils.ConstantManager;
import srgpanov.yandex_test_task.fragments.BookmarksFragment;
import srgpanov.yandex_test_task.fragments.HistoryFragment;
import srgpanov.yandex_test_task.fragments.SettingFragmentContainer;
import srgpanov.yandex_test_task.fragments.TranslateFragment;

import static srgpanov.yandex_test_task.R.id.translate;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mPreferences;

    private android.app.FragmentManager mFragmentManager;
    private TranslateFragment mTranslateFragment;
    private HistoryFragment mHistoryFragment;
    private BookmarksFragment mBookmarksFragment;
    private SettingFragmentContainer mSettingFragmentContainer;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case translate:
                    changeFragment(0);
                    return true;
                case R.id.history:
                    changeFragment(1);
                    return true;
                case R.id.bookmark:
                    changeFragment(2);
                    return true;
                case R.id.settings:
                    //TODO: make settings
                    changeFragment(3);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = YandexAplication.getPreferences();
        mFragmentManager = getFragmentManager();
        mTranslateFragment = new TranslateFragment();
        mHistoryFragment = new HistoryFragment();
        mBookmarksFragment = new BookmarksFragment();
        mSettingFragmentContainer = new SettingFragmentContainer();

        mFragmentManager.beginTransaction()
                .add(R.id.main_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE)
                .commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    private void changeFragment(int position) {

        switch (position) {
            case 0:
                if (!mTranslateFragment.isVisible())
                    mFragmentManager.beginTransaction().replace(R.id.main_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
                break;
            case 1:
                if (!mHistoryFragment.isVisible())
                    mFragmentManager.beginTransaction().replace(R.id.main_container, mHistoryFragment, ConstantManager.FRAGMENT_HISTORY).commit();
                break;
            case 2:
                if (!mBookmarksFragment.isVisible())
                    mFragmentManager.beginTransaction().replace(R.id.main_container, mBookmarksFragment, ConstantManager.FRAGMENT_FAVORITS).commit();
                break;
            case 3:
                if (!mSettingFragmentContainer.isVisible())
                    mFragmentManager.beginTransaction().replace(R.id.main_container, mSettingFragmentContainer, ConstantManager.FRAGMENT_SETTING).commit();
                break;
            default:
                mFragmentManager.beginTransaction().replace(R.id.main_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();

        }

    }

}
