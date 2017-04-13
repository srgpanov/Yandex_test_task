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
    private int mCurrentFragment =1;
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
        loadLastFragment(savedInstanceState);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void loadLastFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction()
                    .add(R.id.fragments_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE)
                    .commit();
        } else {
            mCurrentFragment = savedInstanceState.getInt(ConstantManager.CURRENT_FRAGMENT);
            switch (mCurrentFragment) {
                case 1:
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
                    break;
                case 2:
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mHistoryFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
                    break;
                case 3:
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mBookmarksFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
                    break;
                case 4:
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mSettingFragmentContainer, ConstantManager.FRAGMENT_TRANSLATE).commit();
                    break;
                default:
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.CURRENT_FRAGMENT, mCurrentFragment);
    }

    private void changeFragment(int position) {

        switch (position) {
            case 0:
                if (!mTranslateFragment.isVisible()) {
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
                    mCurrentFragment = 1;
                }
                break;
            case 1:
                if (!mHistoryFragment.isVisible()) {
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mHistoryFragment, ConstantManager.FRAGMENT_HISTORY).commit();
                    mCurrentFragment = 2;
                }
                break;
            case 2:
                if (!mBookmarksFragment.isVisible()) {
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mBookmarksFragment, ConstantManager.FRAGMENT_FAVORITS).commit();
                    mCurrentFragment = 3;
                }
                break;
            case 3:
                if (!mSettingFragmentContainer.isVisible()) {
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mSettingFragmentContainer, ConstantManager.FRAGMENT_SETTING).commit();
                    mCurrentFragment = 4;
                }
                break;
            default:
                mFragmentManager.beginTransaction().replace(R.id.fragments_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();

        }

    }

}
