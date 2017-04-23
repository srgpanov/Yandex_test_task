package srgpanov.yandex_test_task.adapters;

import android.app.Fragment;
import android.app.FragmentManager;

import srgpanov.yandex_test_task.fragments.FavoritsFragment;
import srgpanov.yandex_test_task.fragments.HistoryFragment;
import srgpanov.yandex_test_task.fragments.SettingFragmentContainer;
import srgpanov.yandex_test_task.fragments.TranslateFragment;

/**
 * Created by Пан on 20.04.2017.
 */

/**
 * адаптер для вью педжера гланого активити
 */
public class FragmentPageAdapter extends android.support.v13.app.FragmentPagerAdapter {
    private TranslateFragment mTranslateFragment;
    private HistoryFragment mHistoryFragment;
    private FavoritsFragment mFavoritsFragment;
    private SettingFragmentContainer mSettingFragmentContainer;

    public FragmentPageAdapter(FragmentManager fm) {
        super(fm);
        mTranslateFragment = new TranslateFragment();
        mHistoryFragment = new HistoryFragment();
        mFavoritsFragment = new FavoritsFragment();
        mSettingFragmentContainer = new SettingFragmentContainer();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return mTranslateFragment;
            case 1:return mHistoryFragment;
            case 2:return mFavoritsFragment;
            case 3:return mSettingFragmentContainer;
            default:return null;
        }
    }


    @Override
    public int getCount() {
        return 4;
    }
}
