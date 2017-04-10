package srgpanov.yandex_test_task.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Recognition;
import ru.yandex.speechkit.Recognizer;
import ru.yandex.speechkit.RecognizerListener;
import ru.yandex.speechkit.Synthesis;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.VocalizerListener;
import srgpanov.yandex_test_task.Data.TranslatedWords;
import srgpanov.yandex_test_task.R;
import srgpanov.yandex_test_task.Utils.AvailableLanguages;
import srgpanov.yandex_test_task.Utils.ConstantManager;
import srgpanov.yandex_test_task.Utils.Utils;
import srgpanov.yandex_test_task.YandexEditText;
import srgpanov.yandex_test_task.network.RetroClient;
import srgpanov.yandex_test_task.network.YandexDictApi;
import srgpanov.yandex_test_task.network.YandexTranslateApi;
import srgpanov.yandex_test_task.network.res.LookUpResponse;
import srgpanov.yandex_test_task.network.res.TranslateResponse;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


/**
 * Created by Пан on 27.03.2017.
 */

public class TranslateFragment extends Fragment implements VocalizerListener, RecognizerListener {

    private String mLanguge;
    private int mTimeToTranslate = 3;
    //region views
    private TextView mTranslateOutputTextView;
    private EditText mTranslateInputEditText;
    private ImageView mMic;
    private ImageView mClear;
    private ImageView mInputSpeaker;
    private ImageView mOututSpeaker;
    private ImageView mBookamark;
    private ImageView mShare;
    private ImageView mCopy;
    private RelativeLayout mTranslateContainer;
    private RelativeLayout mDictionaryContainer;
    //endregion
    private Realm mRealm;
    private Timer mTimer;
    private Vocalizer mVocalizer;
    private Recognizer mRecognizer;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);
        mTranslateContainer = (RelativeLayout) rootView.findViewById(R.id.translate_container);
        mDictionaryContainer = (RelativeLayout) rootView.findViewById(R.id.dictionary_container);
        setupButtons(rootView);
        setupTextViews(rootView);

        return rootView;
    }

    private void setupTextViews(View rootView) {
        mTranslateOutputTextView = (TextView) rootView.findViewById(R.id.output_txt_view);
        mTranslateInputEditText = (YandexEditText) rootView.findViewById(R.id.input_edit_text);
        if (TextUtils.isEmpty(mTranslateInputEditText.getText().toString())) {
            mTranslateInputEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        //добавляем TextWatcher для задержки обработки вводимых данных пользователем
        mTranslateInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer.purge();
                }
                resetVocalizer();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final Editable e = editable;
                mTimer = new Timer();
                final String inputLangauge = ((TextView) getActivity().findViewById(R.id.toolbar_left_txt_view)).getText().toString();
                final String outputLangauge = ((TextView) getActivity().findViewById(R.id.toolbar_right_txt_view)).getText().toString();
                //создаём таймер, в кооторый стартует если пользователь не вводит текст 3 секунды
                //TODO: при переключении фрагмента падает приложение
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(e.toString()))
                            translateText(e.toString(), langaugeToAbbreviations(inputLangauge, outputLangauge)); // перевод текста и запись в базу данных запускается в отдельном потоке
                    }
                }, mTimeToTranslate * 1000);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }

        resetRecognizer();
        resetVocalizer();
    }

    private void setupButtons(View rootView) {
        mMic = (ImageView) rootView.findViewById(R.id.ic_mic);
        mInputSpeaker = (ImageView) rootView.findViewById(R.id.ic_speaker_input);
        mClear = (ImageView) rootView.findViewById(R.id.ic_clear);
        mOututSpeaker = (ImageView) rootView.findViewById(R.id.ic_speaker_output);
        mBookamark = (ImageView) rootView.findViewById(R.id.ic_bookmark);
        mShare = (ImageView) rootView.findViewById(R.id.ic_share);
        mCopy = (ImageView) rootView.findViewById(R.id.ic_content_copy);

        mMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndStartRecognizer();
            }
        });
        mInputSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVocalizer != null) {
                    resetVocalizer();
                } else {
                    startSpeech(((TextView) getActivity().findViewById(R.id.toolbar_left_txt_view)).getText().toString(), mTranslateInputEditText.getText().toString());
                }
            }
        });
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTranslateInputEditText.setText(null);
                mTranslateOutputTextView.setText(null);
            }
        });
        mOututSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVocalizer != null) {
                    resetVocalizer();
                } else {
                    startSpeech(((TextView) getActivity().findViewById(R.id.toolbar_right_txt_view)).getText().toString(), mTranslateOutputTextView.getText().toString());
                }
            }
        });
        mBookamark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFavorits();
                //// TODO: 08.04.2017  make yellow icon after click

            }
        });
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "share", Toast.LENGTH_SHORT).show();
            }
        });
        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "copy", Toast.LENGTH_SHORT).show();
                YandexDictApi dictApi = RetroClient.getYandexDictApi();
                Call<LookUpResponse> lookUpResponseCall = dictApi.lookup(ConstantManager.KEY_API_DICT, "en-ru", "time");
                lookUpResponseCall.enqueue(new Callback<LookUpResponse>() {
                    @Override
                    public void onResponse(Call<LookUpResponse> call, Response<LookUpResponse> response) {
                        if (response.code() == 200) {
                            Toast.makeText(getContext(), "555", Toast.LENGTH_SHORT).show();
                            addDictionary(response.body());

                        }
                        if (response.code() != 200)
                            Toast.makeText(getContext(), "666", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<LookUpResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "777", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addDictionary(LookUpResponse body) {
        if (body != null) {
            int tr_LastId = -1;//tr - массив переводов
            int defLastId = -1;//def - массив словарных статей
            int ts_LastId = -1;
            int numeric_tr_LastId = -1;
            int meanLastId = -1;
            for (int i = 0; i < body.getDef().size(); i++) {
                //region делаем вьюху для массива словарных статей
                RelativeLayout.LayoutParams params_def = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params_def.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                if (i != 0 && body.getDef().get(i - 1).getTr().get(body.getDef().get(i - 1).getTr().size() - 1).getMean() != null) {
                    params_def.addRule(RelativeLayout.BELOW, meanLastId);
                } else if (tr_LastId > -1) {
                    params_def.addRule(RelativeLayout.BELOW, tr_LastId);
                } else if (defLastId > -1) {
                    params_def.addRule(RelativeLayout.BELOW, defLastId);
                }
                String defString = body.getDef().get(i).getText()+" ";
                TextView defTextView = new TextView(getContext());
                defTextView.setText(defString);
                defTextView.setPadding(0,dpToPx(8),0,dpToPx(8));
                defTextView.setTextColor(getResources().getColor(R.color.primary_text));
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    defTextView.setId(Utils.generateViewId());
                } else {
                    defTextView.setId(View.generateViewId());
                }
                defTextView.setLayoutParams(params_def);
                mDictionaryContainer.addView(defTextView);
                defLastId = defTextView.getId();
                //endregion
                //region делаем вьюху для массива транскрипций
                if (body.getDef().get(i).getTs() != null) {
                    RelativeLayout.LayoutParams params_ts = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params_ts.addRule(RelativeLayout.RIGHT_OF, defLastId);
                    params_ts.addRule(RelativeLayout.ALIGN_BOTTOM, defLastId);
                    TextView tsTextView = new TextView(getContext());
                    tsTextView.setText("[" + body.getDef().get(i).getTs() + "]");
                    tsTextView.setPadding(0,dpToPx(8),0,dpToPx(8));
                    tsTextView.setTextColor(getResources().getColor(R.color.secondary_text));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        tsTextView.setId(Utils.generateViewId());
                    } else {
                        tsTextView.setId(View.generateViewId());
                    }
                    tsTextView.setLayoutParams(params_ts);
                    mDictionaryContainer.addView(tsTextView);
                    ts_LastId = tsTextView.getId();
                }
                //endregion
                //region делаем вьюху для массива частей речи переводов
                RelativeLayout.LayoutParams params_pos = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (body.getDef().get(i).getPos() != null) {
                    if (ts_LastId != -1) {
                        params_pos.addRule(RelativeLayout.RIGHT_OF, ts_LastId);
                        params_pos.addRule(RelativeLayout.ALIGN_BOTTOM, ts_LastId);
                    } else {
                        params_pos.addRule(RelativeLayout.RIGHT_OF, defLastId);
                        params_pos.addRule(RelativeLayout.ALIGN_BOTTOM, defLastId);
                    }
                    TextView posTextView = new TextView(getContext());
                    posTextView.setText(body.getDef().get(i).getPos());
                    posTextView.setPadding(0,dpToPx(8),0,dpToPx(8));
                    posTextView.setLayoutParams(params_pos);
                    mDictionaryContainer.addView(posTextView);
                }
                //endregion
                for (int j = 0; j < body.getDef().get(i).getTr().size(); j++) {
                    //region делаем вьюху для нумерации масива перевода
                    if (body.getDef().get(i).getTr().size() > 1) {
                        RelativeLayout.LayoutParams params_numeric_tr = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params_numeric_tr.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        if (j == 0) {
                            params_numeric_tr.addRule(RelativeLayout.BELOW, defLastId);
                        } else {
                            if (body.getDef().get(i).getTr().get(j - 1).getMean() != null) {
                                params_numeric_tr.addRule(RelativeLayout.BELOW, meanLastId);
                            } else
                                params_numeric_tr.addRule(RelativeLayout.BELOW, numeric_tr_LastId);
                        }


                        TextView numericTrTextiew = new TextView(getContext());
                        numericTrTextiew.setText(String.valueOf(j + 1));
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            numericTrTextiew.setId(Utils.generateViewId());
                        } else {
                            numericTrTextiew.setId(View.generateViewId());
                        }
                        numericTrTextiew.setLayoutParams(params_numeric_tr);
                        mDictionaryContainer.addView(numericTrTextiew);
                        numeric_tr_LastId = numericTrTextiew.getId();
                    }
                    //endregion
                    //region делаем вьюху для масива перевода и синнимов\
                    //добавляем синнимы
                    String Tr_and_Syn = body.getDef().get(i).getTr().get(j).getText();
                    if (body.getDef().get(i) != null) {
                        if (body.getDef().get(i).getTr().get(j).getSyn() != null) {
                            for (int syn = 0; syn < body.getDef().get(i).getTr().get(j).getSyn().size(); syn++) {
                                Tr_and_Syn = Tr_and_Syn + ", " + body.getDef().get(i).getTr().get(j).getSyn().get(syn).getText();
                            }
                        }
                    }
                    RelativeLayout.LayoutParams params_tr = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    if (body.getDef().get(i).getTr().size() == 1) {
                        params_tr.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        params_tr.addRule(RelativeLayout.BELOW, defLastId);
                    } else {
                        if (numeric_tr_LastId > -1) {
                            params_tr.addRule(RelativeLayout.RIGHT_OF, numeric_tr_LastId);
                            params_tr.addRule(RelativeLayout.ALIGN_BOTTOM, numeric_tr_LastId);
                        }
                    }

                    TextView tr_TextView = new TextView(getContext());
                    tr_TextView.setText(Tr_and_Syn);
                    tr_TextView.setTextColor(getResources().getColor(R.color.blue_for_dictionary));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        tr_TextView.setId(Utils.generateViewId());
                    } else {
                        tr_TextView.setId(View.generateViewId());
                    }
                    tr_TextView.setLayoutParams(params_tr);
                    mDictionaryContainer.addView(tr_TextView);
                    tr_LastId = tr_TextView.getId();
                    //endregion
                    //region делаем вьюху для массива занчений
                    if (body.getDef().get(i).getTr().get(j).getMean() != null) {
                        RelativeLayout.LayoutParams params_mean = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params_mean.addRule(RelativeLayout.ALIGN_LEFT, tr_LastId);
                        params_mean.addRule(RelativeLayout.BELOW, tr_LastId);
                        TextView meanTextView = new TextView(getContext());
                        String meanString = "";
                        for (int mean = 0; mean < body.getDef().get(i).getTr().get(j).getMean().size(); mean++) {
                            if (mean + 1 != body.getDef().get(i).getTr().get(j).getMean().size()) {
                                meanString += body.getDef().get(i).getTr().get(j).getMean().get(mean).getText() + ", ";
                            } else
                                meanString += body.getDef().get(i).getTr().get(j).getMean().get(mean).getText();
                        }
                        meanString+="("+meanString+")";
                        meanTextView.setText(meanString);
                        meanTextView.setTextColor(getResources().getColor(R.color.brown_for_means));
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            meanTextView.setId(Utils.generateViewId());
                        } else {
                            meanTextView.setId(View.generateViewId());
                        }
                        meanTextView.setLayoutParams(params_mean);
                        mDictionaryContainer.addView(meanTextView);
                        meanLastId = meanTextView.getId();
                    }
                    //endregion
                }
            }
        }
    }


    private void createAndStartRecognizer() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), RECORD_AUDIO) != PERMISSION_GRANTED) {
            requestPermissions(new String[]{RECORD_AUDIO}, ConstantManager.REQUEST_PERMISSION_CODE_RECORD_AUDIO);

        } else {
            resetRecognizer();
            //// TODO: 09.04.2017 сделать выбор языка записи
            mRecognizer = Recognizer.create(Recognizer.Language.RUSSIAN, Recognizer.Model.NOTES, this);
            mRecognizer.start();
        }

    }

    private void resetRecognizer() {
        if (mRecognizer != null) {
            mRecognizer.cancel();
            mRecognizer = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;
        switch (requestCode) {
            case ConstantManager.REQUEST_PERMISSION_CODE_RECORD_AUDIO:

                for (int res : grantResults) {
                    // если ползователь дал все разрешения
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // если пользователь не дал разрешения
                allowed = false;
                break;
        }
        if (allowed) {
            createAndStartRecognizer();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(getContext(), R.string.permission_not_allow, Toast.LENGTH_SHORT).show();

                } else {
                    Snackbar.make(mTranslateContainer, R.string.permission_warning, Snackbar.LENGTH_LONG)
                            .setAction(R.string.allow, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openApplicationSettings();
                                }
                            }).show();
                }
            }
        }
    }

    private void startSpeech(String lang, String text) {
        //TODO сделать выбор голоса в настройках
        if (!TextUtils.isEmpty(text)) {
            resetVocalizer();
            if (lang.equals(getResources().getString(R.string.ru))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, Vocalizer.Voice.ZAHAR);
            } else if (lang.equals(getResources().getString(R.string.en))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.ENGLISH, text, true, Vocalizer.Voice.ZAHAR);
            } else if (lang.equals(getResources().getString(R.string.uk))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.UKRAINIAN, text, true, Vocalizer.Voice.ZAHAR);
            } else if (lang.equals(getResources().getString(R.string.tr))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.TURKISH, text, true, Vocalizer.Voice.ZAHAR);
            }
            mVocalizer.setListener(this);
            mVocalizer.start();
        }

    }

    private void resetVocalizer() {
        if (mVocalizer != null) {
            mVocalizer.cancel();
            mVocalizer = null;
        }
    }

    private void addToFavorits() {
        TranslatedWords word = mRealm.where(TranslatedWords.class)
                .equalTo("InputText", mTranslateInputEditText.getText().toString().trim())
                .equalTo("TranslatedText", mTranslateOutputTextView.getText().toString().trim())
                .findFirst();
        if (word == null) {
            final String inputLangauge = ((TextView) getActivity().findViewById(R.id.toolbar_left_txt_view)).getText().toString();
            final String outputLangauge = ((TextView) getActivity().findViewById(R.id.toolbar_right_txt_view)).getText().toString();
            Number currentIdNum = mRealm.where(TranslatedWords.class).max("Id");
            int nextId;
            if (currentIdNum == null) {
                nextId = 1;
            } else {
                nextId = currentIdNum.intValue() + 1;
            }
            mRealm.beginTransaction();
            word = mRealm.createObject(TranslatedWords.class, nextId);
            word.setInputText(mTranslateInputEditText.getText().toString().trim());
            word.setTranslatedText(mTranslateOutputTextView.getText().toString().trim());
            word.setDirectionTranslation(langaugeToAbbreviations(inputLangauge, outputLangauge));
            word.setFavorits(true);
            mRealm.copyToRealmOrUpdate(word);
            mRealm.commitTransaction();
        } else {
            if (!word.isFavorits()) {
                //// TODO: 08.04.2017 если много одинаковых слов не фаворит, то последнее не добавляется в избранное, а остаётся 1 фаворит первое
                mRealm.beginTransaction();
                word.setFavorits(true);
                mRealm.copyToRealmOrUpdate(word);
                mRealm.commitTransaction();
            }
        }
        Toast.makeText(getContext(), R.string.added_to_favorits, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }


    private void addTranslateToDb(String inputText, String translatedText, String directionTranslate, boolean isFavorits) {
        //TODO: make async
        mRealm.beginTransaction();
        Number currentIdNum = mRealm.where(TranslatedWords.class).max("Id");
        int nextId;
        if (currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        TranslatedWords words = mRealm.createObject(TranslatedWords.class, nextId);
        words.setInputText(inputText);
        words.setTranslatedText(translatedText);
        words.setDirectionTranslation(directionTranslate);
        words.setFavorits(isFavorits);
        mRealm.commitTransaction();
    }


    public void translateText(String text, final String lang) {
        //// TODO: make async
        YandexTranslateApi translateApi = RetroClient.getYandexTranslateApi();
        Call<TranslateResponse> translateResponseCall = translateApi.translateText(ConstantManager.KEY_API_TRANSLATE, text, lang);
        translateResponseCall.enqueue(new Callback<TranslateResponse>() {
            @Override
            public void onResponse(Call<TranslateResponse> call, Response<TranslateResponse> response) {
                if (response.code() == 200) {
                    mTranslateOutputTextView.setText(response.body().getText());
                    addTranslateToDb(mTranslateInputEditText.getText().toString().trim(), mTranslateOutputTextView.getText().toString().trim(), lang, false);
                } else {

                    mTranslateOutputTextView.setText(response.toString());
                }
            }

            @Override
            public void onFailure(Call<TranslateResponse> call, Throwable t) {

                Toast.makeText(getContext(), "666", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String langaugeToAbbreviations(String input, String output) {
        AvailableLanguages languages = new AvailableLanguages(getContext());
        ArrayMap<String, Integer> indexLangMap = languages.getLangIndex();
        SparseArray<String> indexAbrMap = languages.getIndexAbr();

        int keyInput = indexLangMap.get(input);
        int keyOutput = indexLangMap.get(output);
        return indexAbrMap.get(keyInput) + "-" + indexAbrMap.get(keyOutput);
    }

    private void openApplicationSettings() {//открываем настройки приложения
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    //region vokalizer.listener
    @Override
    public void onSynthesisBegin(Vocalizer vocalizer) {

    }

    @Override
    public void onSynthesisDone(Vocalizer vocalizer, Synthesis synthesis) {

    }

    @Override
    public void onPlayingBegin(Vocalizer vocalizer) {
        //// TODO: 09.04.2017 make yellow icon
    }

    @Override
    public void onPlayingDone(Vocalizer vocalizer) {
        resetVocalizer();
    }

    @Override
    public void onVocalizerError(Vocalizer vocalizer, Error error) {

    }
    //endregion
    //region recognizer.listener
    @Override
    public void onRecordingBegin(Recognizer recognizer) {
        //// TODO: 09.04.2017 сделать интерфейс записи голоса
        Toast.makeText(getContext(), "begin", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSpeechDetected(Recognizer recognizer) {
        Toast.makeText(getContext(), "detected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSpeechEnds(Recognizer recognizer) {

    }

    @Override
    public void onRecordingDone(Recognizer recognizer) {

        Toast.makeText(getContext(), "RecordingDone", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSoundDataRecorded(Recognizer recognizer, byte[] bytes) {

    }

    @Override
    public void onPowerUpdated(Recognizer recognizer, float v) {

    }

    @Override
    public void onPartialResults(Recognizer recognizer, Recognition recognition, boolean b) {
        String partialText = recognition.getBestResultText() + "...";
        mTranslateInputEditText.setText(partialText);
    }

    @Override
    public void onRecognitionDone(Recognizer recognizer, Recognition recognition) {
        mTranslateInputEditText.setText(recognition.getBestResultText());

    }

    @Override
    public void onError(Recognizer recognizer, Error error) {
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
    }
//endregion

}
