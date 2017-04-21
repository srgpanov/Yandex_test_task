package srgpanov.yandex_test_task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import srgpanov.yandex_test_task.Data.Langauge;

public class InputLangActivity extends AppCompatActivity {
    private RecyclerView mLangRecyclerView;
    private ChooseLangAdapter mAdapter;
    private Toolbar mToolbar;
    Intent answerIntent = new Intent();
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_lang);
        mRealm = Realm.getDefaultInstance();
        mToolbar = (Toolbar) findViewById(R.id.toolbar_input_lang_activity);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ArrayList<Object> objectList = getListForLangAdapter();

        final ArrayList<Object> finalLang = objectList;
        mLangRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_choose_lang);
        mAdapter = new ChooseLangAdapter(objectList, new ChooseLangAdapter.ChoseLangHolder.CustomClickListener() {
            @Override
            public void onLangClickListener(View view, int position) {
                switch (view.getId()) {
                    case R.id.choose_lang_text_view:
                        final Langauge choosenLang = (Langauge) finalLang.get(position);
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (choosenLang != null) {
                                    Number currentIdNum = realm.where(Langauge.class).max("lastUsage");
                                    int nextId;
                                    if (currentIdNum == null) {
                                        nextId = 1;
                                    } else {
                                        nextId = currentIdNum.intValue() + 1;
                                    }
                                    choosenLang.setLastUsage(nextId);
                                }
                            }
                        });
                        answerIntent.putExtra("lang", choosenLang.getName());
                        setResult(RESULT_OK, answerIntent);
                        finish();
                        break;
                    case R.id.choose_lang_image_view:
                        final int itemId = (int) mAdapter.getItemId(position);
                        if (itemId != -1) {
                            mRealm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Langauge langauge = realm.where(Langauge.class).equalTo("Id", itemId).findFirst();
                                    if (langauge != null) {
                                        langauge.setFavorite(!langauge.isFavorite());
                                    }
                                }
                            });
                            mAdapter.notifyItemChanged(position);
                        }
                        break;
                }

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLangRecyclerView.setLayoutManager(layoutManager);
  //      mLangRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mLangRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<Object> getListForLangAdapter() {
        ArrayList<Object> items = new ArrayList<>();
        Number currentIdNum = mRealm.where(Langauge.class).max("lastUsage");
        RealmResults<Langauge> recently_used_lang = mRealm.where(Langauge.class).greaterThan("lastUsage", currentIdNum.intValue() - 3).greaterThan("lastUsage", 0).findAll();
        if (recently_used_lang.size() != 0) {
            items.add(getString(R.string.recently_used));
            for (Langauge lang : recently_used_lang) {
                items.add(lang);
            }
        }
        RealmResults<Langauge> favoritLang = mRealm.where(Langauge.class).equalTo("favorite", true).findAll();
        if (favoritLang.size() != 0) {
            items.add(getString(R.string.favorits));
            for (Langauge lang : favoritLang) {
                items.add(lang);
            }
        }
        RealmResults<Langauge> langaugeRealmResults = mRealm.where(Langauge.class).findAll().sort("name");
        items.add(getString(R.string.all_langauge));
        for (Langauge lang : langaugeRealmResults) {
            items.add(lang);
        }
        return items;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
