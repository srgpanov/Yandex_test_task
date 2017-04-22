package srgpanov.yandex_test_task;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import srgpanov.yandex_test_task.fragments.TranslateFragment;

import static srgpanov.yandex_test_task.R.id.translate;

public class MainActivity extends AppCompatActivity implements OnChoiceItem{

    SharedPreferences mPreferences;
    private DeactivatedViewPager mViewPager;
    private FragmentPageAdapter mPageAdapter;
    BottomNavigationView mNavigation;

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

        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }

    @Override
    public void OnChoiceItem(int Id, boolean isHistory) {
        mViewPager.setCurrentItem(0);
        mNavigation.setSelectedItemId(translate);
        Fragment fragment=getFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + mViewPager.getCurrentItem());
        TranslateFragment translateFragment = (TranslateFragment)fragment;
        translateFragment.showWordFromDb(Id,isHistory);
    }
}
