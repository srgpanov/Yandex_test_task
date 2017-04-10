package srgpanov.yandex_test_task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import srgpanov.yandex_test_task.Utils.AvailableLanguages;

public class InputLangActivity extends AppCompatActivity {
    private RecyclerView mLangRecyclerView;
    private ChooseLangAdapter mAdapter;
    private Toolbar mToolbar;
    Intent answerIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_lang);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_translate);
        setSupportActionBar(mToolbar);
        AvailableLanguages languages = new AvailableLanguages(this);
        ArrayList<String> lang = new ArrayList<>();
        for(int i=0;i<languages.getIndexLang().size();i++){
            lang.add(languages.getIndexLang().valueAt(i));
          }
        Collections.sort(lang);
        final ArrayList<String> finalLang=lang;
        mLangRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_choose_lang);
        mAdapter = new ChooseLangAdapter(lang, new ChooseLangAdapter.ChoseLangHolder.CustomClickListener() {
            @Override
            public void onLangClickListener(View view, int position) {
                switch (view.getId()){
                    case R.id.choose_lang_text_view:
                        answerIntent.putExtra("lang", finalLang.get(position));
                        setResult(RESULT_OK, answerIntent);
                        finish();
                        break;
                    case R.id.choose_lang_image_view:
                        Toast.makeText(getApplicationContext(),"star",Toast.LENGTH_SHORT).show();

                        break;}

                }
            });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLangRecyclerView.setLayoutManager(layoutManager);
        mLangRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mLangRecyclerView.setAdapter(mAdapter);



    }

}
