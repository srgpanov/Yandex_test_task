package srgpanov.yandex_test_task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.yandex.speechkit.SpeechKit;
import srgpanov.yandex_test_task.fragments.BookmarksFragment;
import srgpanov.yandex_test_task.fragments.HistoryFragment;
import srgpanov.yandex_test_task.fragments.SettingsFragment;
import srgpanov.yandex_test_task.fragments.TranslateFragment;

import static srgpanov.yandex_test_task.R.id.translate;
import static srgpanov.yandex_test_task.Utils.ConstantManager.CODE_GET_LANG_INPUT;
import static srgpanov.yandex_test_task.Utils.ConstantManager.CODE_GET_LANG_OUTPUT;
import static srgpanov.yandex_test_task.Utils.ConstantManager.KEY_API_SPEECHKIT;

public class MainActivity extends AppCompatActivity {


    private Toolbar mToolbarTranslate;
    private Toolbar mToolbarHistory;
    private Toolbar mToolbarBookmark;

    private TextView mToolbarLeftTextView;
    private TextView mToolbarRightTextView;
    private ImageView mToolbarImageView;
    private FragmentManager mFragmentManager;
    private TranslateFragment mTranslateFragment;
    private HistoryFragment mHistoryFragment;
    private BookmarksFragment mBookmarksFragment;
    private SettingsFragment mSettingsFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case translate:
                    changeFragment(0);
                    mToolbarHistory.setVisibility(View.GONE);
                    mToolbarBookmark.setVisibility(View.GONE);
                    mToolbarTranslate.setVisibility(View.VISIBLE);
                    return true;
                case R.id.history:
                    changeFragment(1);
                    mToolbarTranslate.setVisibility(View.GONE);
                    mToolbarBookmark.setVisibility(View.GONE);
                    mToolbarHistory.setVisibility(View.VISIBLE);
                    return true;
                case R.id.bookmark:
                    changeFragment(2);
                    mToolbarTranslate.setVisibility(View.GONE);
                    mToolbarHistory.setVisibility(View.GONE);
                    mToolbarBookmark.setVisibility(View.VISIBLE);
                    return true;
                case R.id.settings:
                    //TODO: make settings
//                    changeFragment(3);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        mFragmentManager = getSupportFragmentManager();
        mTranslateFragment = new TranslateFragment();
        mHistoryFragment = new HistoryFragment();
        mBookmarksFragment = new BookmarksFragment();
        mSettingsFragment = new SettingsFragment();

        mFragmentManager.beginTransaction()
                .add(R.id.fragments_container, mTranslateFragment)
                .commit();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);

        SpeechKit.getInstance().configure(getApplicationContext(), KEY_API_SPEECHKIT);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupToolbar() {
        mToolbarTranslate = (Toolbar) findViewById(R.id.toolbar_translate);
        mToolbarHistory = (Toolbar) findViewById(R.id.toolbar_history);
        mToolbarBookmark = (Toolbar) findViewById(R.id.toolbar_bookmark);
        mToolbarLeftTextView = (TextView) mToolbarTranslate.findViewById(R.id.toolbar_left_txt_view);
        mToolbarRightTextView = (TextView) mToolbarTranslate.findViewById(R.id.toolbar_right_txt_view);

        mToolbarImageView = (ImageView) mToolbarTranslate.findViewById(R.id.toolbar_img_view);
        mToolbarLeftTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputLangActivity.class);
                startActivityForResult(intent, CODE_GET_LANG_INPUT);
            }
        });
        mToolbarRightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputLangActivity.class);
                startActivityForResult(intent, CODE_GET_LANG_OUTPUT);
            }
        });
        mToolbarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempLangauge;
                tempLangauge = mToolbarLeftTextView.getText().toString();
                mToolbarLeftTextView.setText(mToolbarRightTextView.getText().toString());
                mToolbarRightTextView.setText(tempLangauge);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_GET_LANG_INPUT) {
            if (resultCode == RESULT_OK) {
                String lang = data.getStringExtra("lang");
                mToolbarLeftTextView.setText(lang);


            }
        }
        if (requestCode == CODE_GET_LANG_OUTPUT) {
            if (resultCode == RESULT_OK) {
                String lang = data.getStringExtra("lang");
                mToolbarRightTextView.setText(lang);

            }
        }
    }


    private void changeFragment(int position) {

        switch (position) {
            case 0:
                if (!mTranslateFragment.isVisible())
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mTranslateFragment).commit();
                break;
            case 1:
                if (!mHistoryFragment.isVisible())
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mHistoryFragment).commit();
                break;
            case 2:
                if (!mBookmarksFragment.isVisible())
                    mFragmentManager.beginTransaction().replace(R.id.fragments_container, mBookmarksFragment).commit();
                break;
            case 3:
                if (!mSettingsFragment.isVisible())
                    mFragmentManager.beginTransaction().replace(R.id.main_container, mSettingsFragment).commit();
                break;
            default:
                mFragmentManager.beginTransaction().replace(R.id.fragments_container, mTranslateFragment).commit();

        }

    }

}
