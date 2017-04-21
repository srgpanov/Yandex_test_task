package srgpanov.yandex_test_task;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import static srgpanov.yandex_test_task.R.id.translate;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mPreferences;
    private DeactivatedViewPager mViewPager;
    private FragmentPageAdapter mPageAdapter;





    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case translate:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.history:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.bookmark:
                    mViewPager.setCurrentItem(2);
                    return true;
                case R.id.settings:
                    //TODO: make settings
                    mViewPager.setCurrentItem(3);
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

        mPageAdapter =new FragmentPageAdapter(getFragmentManager());
        mViewPager = (DeactivatedViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setOffscreenPageLimit(3);
        Fragment fragment=getFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + mViewPager.getCurrentItem());






//        mFragmentManager = getFragmentManager();
//        mTranslateFragment = new TranslateFragment();
//        mHistoryFragment = new HistoryFragment();
//        mFavoritsFragment = new FavoritsFragment();
//        mSettingFragmentContainer = new SettingFragmentContainer();
//        loadLastFragment(savedInstanceState);
//        if (savedInstanceState!=null){
//            wordLastId=savedInstanceState.getInt(ConstantManager.LAST_DICT_ID);
//        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


//    private void loadLastFragment(Bundle savedInstanceState) {
//        if (savedInstanceState == null) {
//            mFragmentManager.beginTransaction()
//                    .add(R.id.fragments_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE)
//                    .commit();
//        } else {
//            mCurrentFragment = savedInstanceState.getInt(ConstantManager.CURRENT_FRAGMENT);
//            switch (mCurrentFragment) {
//                case 1:
//                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
//                    break;
//                case 2:
//                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mHistoryFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
//                    break;
//                case 3:
//                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mFavoritsFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
//                    break;
//                case 4:
//                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mSettingFragmentContainer, ConstantManager.FRAGMENT_TRANSLATE).commit();
//                    break;
//                default:
//                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
//            }
//
//        }
//    }

//    private void changeFragment(int position) {
//
//        switch (position) {
//            case 0:
//                if (!mTranslateFragment.isVisible()) {
//                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
//                    mCurrentFragment = 1;
//                }
//                break;
//            case 1:
//                if (!mHistoryFragment.isVisible()) {
//                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mHistoryFragment, ConstantManager.FRAGMENT_HISTORY).commit();
//                    mCurrentFragment = 2;
//                }
//                break;
//            case 2:
//                if (!mFavoritsFragment.isVisible()) {
//                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mFavoritsFragment, ConstantManager.FRAGMENT_FAVORITS).commit();
//                    mCurrentFragment = 3;
//                }
//                break;
//            case 3:
//                if (!mSettingFragmentContainer.isVisible()) {
//                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mSettingFragmentContainer, ConstantManager.FRAGMENT_SETTING).commit();
//                    mCurrentFragment = 4;
//                }
//                break;
//            default:
//                mFragmentManager.beginTransaction().replace(R.id.fragments_container, mTranslateFragment, ConstantManager.FRAGMENT_TRANSLATE).commit();
//
//        }
//
//    }




}
