package srgpanov.yandex_test_task.fragments;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
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
import srgpanov.yandex_test_task.Data.Dictionary.Defenition;
import srgpanov.yandex_test_task.Data.Dictionary.Means;
import srgpanov.yandex_test_task.Data.Dictionary.Synonymous;
import srgpanov.yandex_test_task.Data.Dictionary.Transcript;
import srgpanov.yandex_test_task.Data.FavoritsWord;
import srgpanov.yandex_test_task.Data.Langauge;
import srgpanov.yandex_test_task.Data.TranslatedWords;
import srgpanov.yandex_test_task.activities.InputLangActivity;
import srgpanov.yandex_test_task.R;
import srgpanov.yandex_test_task.Utils.ConstantManager;
import srgpanov.yandex_test_task.Utils.Utils;
import srgpanov.yandex_test_task.YandexAplication;
import srgpanov.yandex_test_task.Utils.YandexEditText;
import srgpanov.yandex_test_task.network.RetroClient;
import srgpanov.yandex_test_task.network.YandexDictApi;
import srgpanov.yandex_test_task.network.YandexTranslateApi;
import srgpanov.yandex_test_task.network.res.LookUpResponse;
import srgpanov.yandex_test_task.network.res.TranslateResponse;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static srgpanov.yandex_test_task.R.string.en;
import static srgpanov.yandex_test_task.R.string.tr;
import static srgpanov.yandex_test_task.Utils.ConstantManager.CODE_GET_LANG_INPUT;
import static srgpanov.yandex_test_task.Utils.ConstantManager.CODE_GET_LANG_OUTPUT;


/**
 * Created by Пан on 27.03.2017.
 */

public class TranslateFragment extends android.app.Fragment implements VocalizerListener, RecognizerListener {


    //region views
    private TextView mTranslateOutputTextView;
    private YandexEditText mTranslateInputEditText;
    private ImageView mMic;
    private ImageView mClear;
    private ImageView mInputSpeaker;
    private ImageView mOututSpeaker;
    private ImageView mFavorite;
    private ImageView mShare;
    private ImageView mCopy;
    private ImageView mToolbarImageView;
    private TextView mToolbarLeftTextView;
    private TextView mToolbarRightTextView;
    private RelativeLayout mTranslateContainer;
    private RelativeLayout mDictionaryContainer;
    //endregion

    private SharedPreferences mPreferences;
    private Realm mRealm;
    private CountDownTimer mTimer;
    //задержка до перевода
    private int MtimerDelay;
    private Vocalizer mVocalizer;
    private Recognizer mRecognizer;
    private int lastDictId = -1; //Id последнего словарного слова, чтобы при перевороте можно было восстановить состояние
    RealmResults<Langauge> mLangauge;
    //переключатели состояния кнопок
    private boolean isFavoritWord = false;
    private boolean isSpeakerInput = false;
    private boolean isSpeakerOutput = false;
    //список доступных направлений для словаря
    private String[] mLangForDict = {"be-be", "be-ru", "bg-ru", "cs-en", "cs-ru", "da-en", "da-ru", "de-de", "de-en", "de-ru", "de-tr", "el-en", "el-ru", "en-cs", "en-da", "en-de", "en-el", "en-en", "en-es", "en-et", "en-fi", "en-fr", "en-it", "en-lt", "en-lv", "en-nl", "en-no", "en-pt", "en-ru", "en-sk", "en-sv", "en-tr", "en-uk", "es-en", "es-es", "es-ru", "et-en", "et-ru", "fi-en", "fi-fi", "fi-ru", "fr-en", "fr-fr", "fr-ru", "hu-hu", "hu-ru", "it-en", "it-it", "it-ru", "lt-en", "lt-lt", "lt-ru", "lv-en", "lv-ru", "mhr-ru", "mrj-ru", "nl-en", "nl-ru", "no-en", "no-ru", "pl-ru", "pt-en", "pt-ru", "ru-be", "ru-bg", "ru-cs", "ru-da", "ru-de", "ru-el", "ru-en", "ru-es", "ru-et", "ru-fi", "ru-fr", "ru-hu", "ru-it", "ru-lt", "ru-lv", "ru-mhr", "ru-mrj", "ru-nl", "ru-no", "ru-pl", "ru-pt", "ru-ru", "ru-sk", "ru-sv", "ru-tr", "ru-tt", "ru-uk", "sk-en", "sk-ru", "sv-en", "sv-ru", "tr-de", "tr-en", "tr-ru", "tt-ru", "uk-en", "uk-ru", "uk-uk"};


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = YandexAplication.getPreferences();
        MtimerDelay = mPreferences.getInt(ConstantManager.DELAY_TO_TRANSLATE, ConstantManager.DELAY_NORMAL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRealm = Realm.getDefaultInstance();
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);
        mTranslateContainer = (RelativeLayout) rootView.findViewById(R.id.translate_container);
        mDictionaryContainer = (RelativeLayout) rootView.findViewById(R.id.dictionary_container);
        mToolbarLeftTextView = (TextView) rootView.findViewById(R.id.toolbar_left_txt_view);
        mToolbarRightTextView = (TextView) rootView.findViewById(R.id.toolbar_right_txt_view);
        mTranslateOutputTextView = (TextView) rootView.findViewById(R.id.output_txt_view);
        mTranslateInputEditText = (YandexEditText) rootView.findViewById(R.id.input_edit_text);
        mTranslateInputEditText.requestFocus();
        mLangauge = mRealm.where(Langauge.class).findAllAsync();
        setupButtons(rootView);
        setupToolbar(rootView);
        return rootView;
    }
//устанавливаем кнопку любимого слова  при просмотре из истории или избранного
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFavoritWord) {
                final FavoritsWord word = mRealm.where(FavoritsWord.class)
                        .equalTo("InputText", mTranslateInputEditText.getText().toString())
                        .equalTo("TranslatedText", mTranslateOutputTextView.getText().toString())
                        .findFirst();
                if (word == null) {
                    mFavorite.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                    isFavoritWord = false;
                }

            }
        }
    }
//восстанавливаем состояние при повороте
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            mTranslateOutputTextView.setText(savedInstanceState.getString(ConstantManager.OUTPUT_TEXT_VIEW));
            lastDictId = savedInstanceState.getInt(ConstantManager.LAST_DICT_ID, -1);
            if (lastDictId != -1) {
                final TranslatedWords lastDictWords = mRealm.where(TranslatedWords.class).equalTo("Id", lastDictId).findFirst();
                if (lastDictWords != null) addDictionary(lastDictWords);
                else
                    lookInDictionary(savedInstanceState.getString(ConstantManager.OUTPUT_TEXT_VIEW), savedInstanceState.getString(ConstantManager.LAST_LANG));
            }
            changeButtonsShown(mTranslateInputEditText.getText().toString(), mTranslateOutputTextView.getText().toString());
            mToolbarLeftTextView.setText(savedInstanceState.getString(ConstantManager.LAST_INLANG));
            mToolbarRightTextView.setText(savedInstanceState.getString(ConstantManager.LAST_OUTLANG));
        }
    }
//сохраняем состояние при повороте экрана
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTranslateOutputTextView != null) {
            outState.putString(ConstantManager.OUTPUT_TEXT_VIEW, mTranslateOutputTextView.getText().toString());
        }
        outState.putInt(ConstantManager.LAST_DICT_ID, lastDictId);
        outState.putString(ConstantManager.LAST_LANG, getDirection());
        outState.putString(ConstantManager.LAST_INLANG, mToolbarLeftTextView.getText().toString());
        outState.putString(ConstantManager.LAST_OUTLANG, mToolbarRightTextView.getText().toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        mTranslateInputEditText.addTextChangedListener(mInWatcher);
        mTranslateOutputTextView.addTextChangedListener(mOutWatcher);
    }
//метод изменяет видимость кнопок
    private void changeButtonsShown(String inText, String outText) {
        if (!TextUtils.isEmpty(outText)) {
            mOututSpeaker.setVisibility(View.VISIBLE);
            mFavorite.setVisibility(View.VISIBLE);
            mShare.setVisibility(View.VISIBLE);
            mCopy.setVisibility(View.VISIBLE);
        } else {
            mOututSpeaker.setVisibility(View.GONE);
            mFavorite.setVisibility(View.GONE);
            mShare.setVisibility(View.GONE);
            mCopy.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(inText)) {
            mInputSpeaker.setVisibility(View.VISIBLE);
        } else {
            mInputSpeaker.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(outText) && TextUtils.isEmpty(inText)) {
            mClear.setVisibility(View.GONE);
        } else mClear.setVisibility(View.VISIBLE);
    }

//настаиваем тулбар
    private void setupToolbar(View rootview) {
        mToolbarLeftTextView = (TextView) rootview.findViewById(R.id.toolbar_left_txt_view);
        mToolbarRightTextView = (TextView) rootview.findViewById(R.id.toolbar_right_txt_view);
        mToolbarImageView = (ImageView) rootview.findViewById(R.id.toolbar_img_view);
        mToolbarLeftTextView.setText(mPreferences.getString(ConstantManager.TOOLBAR_LEFT_TEXT_VIEW, getResources().getString(R.string.english)));
        mToolbarRightTextView.setText(mPreferences.getString(ConstantManager.TOOLBAR_RIGHT_TEXT_VIEW, getResources().getString(R.string.russian)));
        mToolbarLeftTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InputLangActivity.class);
                startActivityForResult(intent, CODE_GET_LANG_INPUT);
            }
        });
        mToolbarRightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InputLangActivity.class);
                startActivityForResult(intent, CODE_GET_LANG_OUTPUT);
            }
        });
        mToolbarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempLangauge;//меняем местами направление перевода и переведённый текст переноси в поле для перевода
                tempLangauge = mToolbarLeftTextView.getText().toString();
                mToolbarLeftTextView.setText(mToolbarRightTextView.getText().toString());
                mToolbarRightTextView.setText(tempLangauge);
                SharedPreferences.Editor editor = mPreferences.edit();//сохраняем последнее направление перевода
                editor.putString(ConstantManager.TOOLBAR_LEFT_TEXT_VIEW, mToolbarLeftTextView.getText().toString());
                editor.putString(ConstantManager.TOOLBAR_RIGHT_TEXT_VIEW, mToolbarRightTextView.getText().toString());
                editor.apply();
                swapTranslatedText();
                resetDictionary();
            }
        });
    }
//получаем и устанавливаем выбранный язык в тулбар
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_GET_LANG_INPUT) {
            if (resultCode == RESULT_OK) {
                String lang = data.getStringExtra("lang");
                mToolbarLeftTextView.setText(lang);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(ConstantManager.TOOLBAR_LEFT_TEXT_VIEW, mToolbarLeftTextView.getText().toString());
                editor.apply();

            }
        }
        if (requestCode == CODE_GET_LANG_OUTPUT) {
            if (resultCode == RESULT_OK) {
                String lang = data.getStringExtra("lang");
                mToolbarRightTextView.setText(lang);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(ConstantManager.TOOLBAR_RIGHT_TEXT_VIEW, mToolbarRightTextView.getText().toString());
                editor.apply();
            }
        }

    }


    @Override
    public void onPause() {
        super.onPause();

        if (mTimer != null) {
            mTimer.cancel();
        }
        resetRecognizer();
        resetVocalizer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRealm.close();
    }
//обрабатываем ресултат запроса разрешений
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
        if (allowed) {//если разрешения есть стартуем голосовой ввод
            createAndStartRecognizer();
        } else {//если требуемых разрешений нет и андроид версии>6 выводим снекбар с кнопкой открытия настроек
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(getActivity(), R.string.permission_not_allow, Toast.LENGTH_SHORT).show();

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

//метод вызывается главным активити для показа слова из истории или избранного
    public void showWordFromDb(int Id, boolean isHistory) {
        mTranslateOutputTextView.removeTextChangedListener(mOutWatcher);//отключаем листенеры, чтобы не было повторно запроса для сохранённого слова
        mTranslateInputEditText.removeTextChangedListener(mInWatcher);
        if (isHistory) {
            TranslatedWords words = mRealm.where(TranslatedWords.class).equalTo("Id", Id).findFirst();
            if (words != null) {

                mTranslateInputEditText.setText(words.getInputText());
                mTranslateOutputTextView.setText(words.getTranslatedText());
                addDictionary(words);
                mToolbarLeftTextView.setText(convertAbreviations(words.getDirectionTranslation())[0]);
                mToolbarRightTextView.setText(convertAbreviations(words.getDirectionTranslation())[1]);
                if (words.isFavorits()) {
                    isFavoritWord = true;
                    mFavorite.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
                }
                lastDictId = words.getId();
            }
        } else {
            FavoritsWord words = mRealm.where(FavoritsWord.class).equalTo("Id", Id).findFirst();
            if (words != null) {
                mTranslateInputEditText.setText(words.getInputText());
                mTranslateOutputTextView.setText(words.getTranslatedText());
                TranslatedWords translatedWords = new TranslatedWords();
                translatedWords.setInputText(words.getInputText());
                translatedWords.setDirectionTranslation(words.getDirectionTranslation());
                translatedWords.setTranslatedText(words.getTranslatedText());
                translatedWords.setFavorits(words.isFavorits());
                translatedWords.setDefenitions(words.getDefenitions());
                addDictionary(translatedWords);
                mToolbarLeftTextView.setText(convertAbreviations(words.getDirectionTranslation())[0]);
                mToolbarRightTextView.setText(convertAbreviations(words.getDirectionTranslation())[1]);
                if (words.isFavorits()) {
                    isFavoritWord = true;
                    mFavorite.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
                }
                lastDictId = words.getId();
            }

        }
        changeButtonsShown(mTranslateInputEditText.getText().toString(), mTranslateOutputTextView.getText().toString());
        mTranslateOutputTextView.addTextChangedListener(mOutWatcher);
        mTranslateInputEditText.addTextChangedListener(mInWatcher);
    }

//настраиваем кнопки
    private void setupButtons(View rootView) {
        mMic = (ImageView) rootView.findViewById(R.id.ic_mic);
        mInputSpeaker = (ImageView) rootView.findViewById(R.id.ic_speaker_input);
        mClear = (ImageView) rootView.findViewById(R.id.ic_clear);
        mOututSpeaker = (ImageView) rootView.findViewById(R.id.ic_speaker_output);
        mFavorite = (ImageView) rootView.findViewById(R.id.ic_bookmark);
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
                    isSpeakerInput = false;
                    mInputSpeaker.setImageResource(R.drawable.ic_speaker_grey_24dp);
                } else {
                    isSpeakerInput = true;
                    mInputSpeaker.setImageResource(R.drawable.ic_speaker_yellow_24dp);
                    startSpeech(mToolbarLeftTextView.getText().toString(), mTranslateInputEditText.getText().toString());
                }
            }
        });
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTranslateInputEditText.setText(null);
                mTranslateOutputTextView.setText(null);
                resetDictionary();
            }
        });
        mOututSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVocalizer != null) {
                    resetVocalizer();
                    isSpeakerOutput = false;
                    mOututSpeaker.setImageResource(R.drawable.ic_speaker_grey_24dp);
                } else {
                    isSpeakerOutput = true;
                    mOututSpeaker.setImageResource(R.drawable.ic_speaker_yellow_24dp);
                    startSpeech(mToolbarRightTextView.getText().toString(), mTranslateOutputTextView.getText().toString());
                }
            }
        });
        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFavoritWord) {
                    mFavorite.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
                    addToDb(mTranslateInputEditText.getText().toString().trim(), mTranslateOutputTextView.getText().toString().trim(), getDirection(), true);
                    isFavoritWord = true;
                } else {
                    mFavorite.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                    addToDb(mTranslateInputEditText.getText().toString().trim(), mTranslateOutputTextView.getText().toString().trim(), getDirection(), false);
                    deleteFromFavorits(mTranslateInputEditText.getText().toString(), mTranslateOutputTextView.getText().toString());
                    isFavoritWord = false;
                }
            }
        });
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String textToSend = mTranslateOutputTextView.getText().toString();
                intent.putExtra(Intent.EXTRA_TEXT, textToSend);
                try {
                    startActivity(Intent.createChooser(intent, getString(R.string.send_translate)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Some error", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("translate", mTranslateOutputTextView.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getActivity(), R.string.copied, Toast.LENGTH_SHORT).show();
            }
        });
    }
//удаляем слово из избранного асинхронно
    private void deleteFromFavorits(final String inputText, final String outpuText) {
        final Realm newRealm = Realm.getDefaultInstance();
        newRealm.executeTransactionAsync(new Realm.Transaction() {
                                             @Override
                                             public void execute(Realm realm) {
                                                 FavoritsWord word = realm.where(FavoritsWord.class)
                                                         .equalTo("InputText", inputText)
                                                         .equalTo("TranslatedText", outpuText)
                                                         .findFirst();
                                                 if (word != null) {
                                                     word.getHistoryWords().setFavorits(false);//слово в истории помечаем как не избранное
                                                     word.deleteFromRealm();
                                                 }
                                             }
                                         }, new Realm.Transaction.OnSuccess() {
                                             @Override
                                             public void onSuccess() {
                                                 newRealm.close();
                                             }
                                         }, new Realm.Transaction.OnError() {
                                             @Override
                                             public void onError(Throwable error) {
                                                 newRealm.close();
                                             }
                                         }
        );
    }

//метод для голосового ввода
    private void createAndStartRecognizer() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), RECORD_AUDIO) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{RECORD_AUDIO}, ConstantManager.REQUEST_PERMISSION_CODE_RECORD_AUDIO);

        } else {
            resetRecognizer();
            //// TODO: 09.04.2017 сделать выбор языка записи
            mRecognizer = Recognizer.create(Recognizer.Language.RUSSIAN, Recognizer.Model.NOTES, this);
            mRecognizer.start();
        }

    }
//синтез речи
    private void startSpeech(String lang, String text) {
        if (!TextUtils.isEmpty(text)) {
            resetVocalizer();
            if (lang.equals(getResources().getString(R.string.ru))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, mPreferences.getString(ConstantManager.SPEECH_VOICE, ConstantManager.SPEECH_VOICE_ZAHAR));
            } else if (lang.equals(getResources().getString(en))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.ENGLISH, text, true, mPreferences.getString(ConstantManager.SPEECH_VOICE, ConstantManager.SPEECH_VOICE_ZAHAR));
            } else if (lang.equals(getResources().getString(R.string.uk))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.UKRAINIAN, text, true, mPreferences.getString(ConstantManager.SPEECH_VOICE, ConstantManager.SPEECH_VOICE_ZAHAR));
            } else if (lang.equals(getResources().getString(tr))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.TURKISH, text, true, mPreferences.getString(ConstantManager.SPEECH_VOICE, ConstantManager.SPEECH_VOICE_ZAHAR));
            }
            if (mVocalizer != null) {
                mVocalizer.setListener(this);
                mVocalizer.start();
            } else {
                mInputSpeaker.setImageResource(R.drawable.ic_speaker_grey_24dp);
                mOututSpeaker.setImageResource(R.drawable.ic_speaker_grey_24dp);
                isSpeakerOutput = false;
                isSpeakerInput = false;
                showToast(getString(R.string.speech_synthesis_is_not_supported));
            }
        }
    }
//переводим текст
    public void translateText(String text, final String lang) {
        if (Utils.isNetworkAvailable(getActivity())) {//проверяем доступность интернета
            YandexTranslateApi translateApi = RetroClient.getYandexTranslateApi();
            Call<TranslateResponse> translateResponseCall = translateApi.translateText(ConstantManager.KEY_API_TRANSLATE, text, lang);
            translateResponseCall.enqueue(new Callback<TranslateResponse>() {
                @Override
                public void onResponse(Call<TranslateResponse> call, Response<TranslateResponse> response) {
                    if (response.isSuccessful()) {
                        mTranslateOutputTextView.setText(response.body().getText());
                    } else if (response.code() == 401) {
                        showToast(getString(R.string.err_tr_401));//обрабатываем ответы и выводим в тоаст
                    } else if (response.code() == 402) {
                        showToast(getString(R.string.err_tr_402));
                    } else if (response.code() == 404) {
                        showToast(getString(R.string.err_tr_404));
                    } else if (response.code() == 413) {
                        showToast(getString(R.string.err_tr_413));
                    } else if (response.code() == 422) {
                        showToast(getString(R.string.err_tr_422));
                    } else if (response.code() == 501) {
                        showToast(getString(R.string.err_tr_501));
                    }
                }

                @Override
                public void onFailure(Call<TranslateResponse> call, Throwable t) {

                    showToast(getString(R.string.unknown_error_tr));
                }
            });
        } else {
            Snackbar.make(mTranslateContainer, R.string.check_intenet_conection, Snackbar.LENGTH_SHORT).show();
        }
    }

    public void swapTranslatedText() {
        mTranslateInputEditText.setText(mTranslateOutputTextView.getText().toString());
        mTranslateOutputTextView.setText("");
    }


//делаем запрос к словарю Яндекса
    public void lookInDictionary(final String text, final String lang) {
        if (Utils.isNetworkAvailable(getActivity())) {//проверяем интернет
            if (!Utils.isMoreFourWords(text) && !TextUtils.isEmpty(text)) {//проверяем чтобы было меньше 4 слов, текст был не пустой
                YandexDictApi dictApi = RetroClient.getYandexDictApi();
                Call<LookUpResponse> lookUpResponseCall = dictApi.lookup(ConstantManager.KEY_API_DICT, lang, text);
                lookUpResponseCall.enqueue(new Callback<LookUpResponse>() {
                    @Override
                    public void onResponse(Call<LookUpResponse> call, Response<LookUpResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                addDictionaryDb(response.body(), text, lang); //записываем ответ в базу данных
                            }
                        } else if (response.code() == 401) {//обрабатываем ответы сервера и выводим в тоаст
                            showToast(getString(R.string.err_dict_401));
                        } else if (response.code() == 402) {
                            showToast(getString(R.string.err_dict_402));
                        } else if (response.code() == 403) {
                            showToast(getString(R.string.err_dict_403));
                        } else if (response.code() == 413) {
                            showToast(getString(R.string.err_dict_413));
                        }
                    }

                    @Override
                    public void onFailure(Call<LookUpResponse> call, Throwable t) {
                        showToast(getString(R.string.unknown_error_tr));
                    }
                });
            }
        } else {
            Snackbar.make(mTranslateContainer, R.string.check_intenet_conection, Snackbar.LENGTH_SHORT).show();
        }
    }



    /**
     *
     * @param body ответ от сервера
     * @param text текст словарного слова
     * @param lang направление языка
     */
    private void addDictionaryDb(final LookUpResponse body, final String text, final String lang) { //   записываем ответ в базу данных яндекс словаря в базу данных
        final List<LookUpResponse.Def> defList = body.getDef();
        final Realm newRealm = Realm.getDefaultInstance();
        Realm.Transaction transactionDict = new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TranslatedWords words = realm.where(TranslatedWords.class).equalTo("InputText", text).equalTo("DirectionTranslation", lang).findFirst();//ищем слово в базе данных с заданными параметрами
                if (words != null) {                                                                                                                    // и записываем в него все поля в цикле
                    RealmList<Defenition> defenitionList = new RealmList<>();
                    for (int i = 0; i < defList.size(); i++) {
                        Defenition newDefenition = realm.createObject(Defenition.class);
                        newDefenition.setWords(words);
                        newDefenition.setText(defList.get(i).getText());
                        newDefenition.setPos(defList.get(i).getPos());
                        newDefenition.setTranscription(defList.get(i).getTs());
                        if (defList.get(i).getTr() != null) {
                            RealmList<Transcript> transcriptList = new RealmList<>();
                            for (int j = 0; j < defList.get(i).getTr().size(); j++) {
                                Transcript newTranscript = realm.createObject(Transcript.class);
                                newTranscript.setDefenition(newDefenition);
                                newTranscript.setText(defList.get(i).getTr().get(j).getText());
                                newTranscript.setPos(defList.get(i).getTr().get(j).getPos());
                                newTranscript.setGender(defList.get(i).getTr().get(j).getPos());
                                newTranscript.setText(defList.get(i).getTr().get(j).getText());
                                if (defList.get(i).getTr().get(j).getSyn() != null) {
                                    RealmList<Synonymous> synonymousList = new RealmList<>();
                                    for (int syn = 0; syn < defList.get(i).getTr().get(j).getSyn().size(); syn++) {
                                        Synonymous newSynonymous = realm.createObject(Synonymous.class);
                                        newSynonymous.setTranscript(newTranscript);
                                        newSynonymous.setText(defList.get(i).getTr().get(j).getSyn().get(syn).getText());
                                        newSynonymous.setGender(defList.get(i).getTr().get(j).getSyn().get(syn).getGen());
                                        newSynonymous.setPos(defList.get(i).getTr().get(j).getSyn().get(syn).getPos());
                                        synonymousList.add(newSynonymous);
                                    }
                                    newTranscript.setSyn(synonymousList);
                                }
                                if (defList.get(i).getTr().get(j).getMean() != null) {
                                    RealmList<Means> meansList = new RealmList<>();
                                    for (int mean = 0; mean < defList.get(i).getTr().get(j).getMean().size(); mean++) {
                                        Means newMeans = realm.createObject(Means.class);
                                        newMeans.setTranscript(newTranscript);
                                        newMeans.setText(defList.get(i).getTr().get(j).getMean().get(mean).getText());
                                        meansList.add(newMeans);
                                    }
                                    newTranscript.setMean(meansList);
                                }
                                transcriptList.add(newTranscript);
                            }
                            newDefenition.setTr(transcriptList);
                        }

                        defenitionList.add(newDefenition);

                        words.setDefenitions(defenitionList);
                        if (words.getFavoritsWord() != null) {//если есть связанное избарнное слово добавляем словарную статью в него
                            words.getFavoritsWord().setDefenitions(defenitionList);
                        }
                    }
                }

            }

        };
        Realm.Transaction.OnSuccess onSuccessAddDict = new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                final TranslatedWords words = newRealm.where(TranslatedWords.class).equalTo("InputText", text).equalTo("DirectionTranslation", lang).findFirstAsync();
                words.addChangeListener(new RealmChangeListener<RealmModel>() {
                    @Override
                    public void onChange(RealmModel element) {
                        addDictionary(words);//выводим словарную статью на экран
                        words.removeAllChangeListeners();
                        newRealm.close();
                    }
                });
            }
        };
        Realm.Transaction.OnError onErrorAddDict = new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                newRealm.close();
            }
        };

        newRealm.executeTransactionAsync(transactionDict, onSuccessAddDict, onErrorAddDict);
    }

    /**
     * добавлям слово в БД
     * @param inputText
     * @param translatedText
     * @param directionTranslate направление перевода
     * @param addfavorits добавлять в избранное или нет
     */
    private void addToDb(final String inputText, final String translatedText, final String directionTranslate, final boolean addfavorits) {
        if (!TextUtils.isEmpty(inputText) && !TextUtils.isEmpty(translatedText)) {
            final Realm newRealm = Realm.getDefaultInstance();
            newRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TranslatedWords word = realm.where(TranslatedWords.class)//ищем слово с заданными параметрами в БД
                            .equalTo("InputText", inputText)
                            .equalTo("TranslatedText", translatedText)
                            .findFirst();
                    if (word == null) {//если его нет добавляем его в БД
                        Number currentIdNum = realm.where(TranslatedWords.class).max("Id");
                        int nextId;
                        if (currentIdNum == null) {
                            nextId = 1;
                        } else {
                            nextId = currentIdNum.intValue() + 1;
                        }
                        TranslatedWords newHistoryWord = realm.createObject(TranslatedWords.class, nextId);
                        newHistoryWord.setInputText(inputText);
                        newHistoryWord.setTranslatedText(translatedText);
                        newHistoryWord.setDirectionTranslation(directionTranslate);
                        newHistoryWord.setFavorits(false);
                        realm.copyToRealmOrUpdate(newHistoryWord);
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    newRealm.close();
                    if (addfavorits) //добавляем слово в избранное
                        addToFavorits(inputText, translatedText, directionTranslate);
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {

                    newRealm.close();
                }
            });
        }
    }
//добавляем слово в избранное
    private void addToFavorits(final String inputText, final String translatedText, final String directionTranslate) {
        if (!TextUtils.isEmpty(inputText) && !TextUtils.isEmpty(translatedText)) {//проверяем чтобы поля не были пустыми
            final Realm newRealm = Realm.getDefaultInstance();
            newRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    FavoritsWord word = realm.where(FavoritsWord.class)//ищем слово с заданными параметрами в БД
                            .equalTo("InputText", inputText)
                            .equalTo("TranslatedText", translatedText)
                            .findFirst();
                    if (word == null) {
                        Number currentIdNum = realm.where(FavoritsWord.class).max("Id");
                        int nextId;
                        if (currentIdNum == null) {
                            nextId = 1;
                        } else {
                            nextId = currentIdNum.intValue() + 1;
                        }
                        FavoritsWord newFavoritWord = realm.createObject(FavoritsWord.class, nextId);
                        newFavoritWord.setInputText(inputText);
                        newFavoritWord.setTranslatedText(translatedText);
                        newFavoritWord.setDirectionTranslation(directionTranslate);
                        newFavoritWord.setFavorits(true);
                        realm.copyToRealmOrUpdate(newFavoritWord);
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    newRealm.close();
                    final Realm newRealm = Realm.getDefaultInstance();
                    //связываем слова из избранного и из истории
                    newRealm.executeTransactionAsync(new Realm.Transaction() {
                                                         @Override
                                                         public void execute(Realm realm) {
                                                             FavoritsWord lastFavoritsWord = realm.where(FavoritsWord.class)
                                                                     .equalTo("InputText", inputText)
                                                                     .equalTo("TranslatedText", translatedText)
                                                                     .findFirst();
                                                             TranslatedWords lastHistoryWord = realm.where(TranslatedWords.class)
                                                                     .equalTo("InputText", inputText)
                                                                     .equalTo("TranslatedText", translatedText)
                                                                     .findFirst();
                                                             lastHistoryWord.setFavorits(true);
                                                             lastFavoritsWord.setHistoryWords(lastHistoryWord);
                                                             lastHistoryWord.setFavoritsWord(lastFavoritsWord);
                                                         }
                                                     }, new Realm.Transaction.OnSuccess() {
                                                         @Override
                                                         public void onSuccess() {
                                                             newRealm.close();
                                                         }
                                                     }, new Realm.Transaction.OnError() {
                                                         @Override
                                                         public void onError(Throwable error) {
                                                             newRealm.close();
                                                         }
                                                     }
                    );

                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    newRealm.close();
                }
            });
        }
    }

//метод открывает настройки приложения в системных настройках
    private void openApplicationSettings() {//открываем настройки приложения
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }


    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void resetDictionary() {
        if (mDictionaryContainer.getChildCount() != 0) {
            mDictionaryContainer.removeAllViews();
        }
    }

    private void resetRecognizer() {
        if (mRecognizer != null) {
            mRecognizer.cancel();
            mRecognizer = null;
        }
    }

    private void resetVocalizer() {
        if (mVocalizer != null) {
            mVocalizer.cancel();
            mVocalizer = null;
        }
    }
//метод преобразовывает назавние языка в абревиатуры и склеивает, возвращает направление перевода для запроса
    private String getDirection() {
        String inputLangauge = mLangauge.where().equalTo("name", mToolbarLeftTextView.getText().toString()).findFirst().getAbbreviation();// не стал делатьт поиск асинхронно, так как всего 90 языкоа
        String outputLangauge = mLangauge.where().equalTo("name", mToolbarRightTextView.getText().toString()).findFirst().getAbbreviation();
        return inputLangauge + "-" + outputLangauge;
    }

    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
    //метод преобразовывает абревиатуры языка в названия, возвращет массив где 0 элемент язык переводиого слова, 1 элемент язык на который переводится текст
    private String[] convertAbreviations(String directionLang) {
        String[] lang = directionLang.split("-");
        String inLang = lang[0];
        String outLang = lang[1];
        Realm realm = Realm.getDefaultInstance();
        lang[0] = realm.where(Langauge.class).equalTo("abbreviation", inLang).findFirst().getName();
        lang[1] = realm.where(Langauge.class).equalTo("abbreviation", outLang).findFirst().getName();
        realm.close();
        return lang;
    }

//листенер для поля перевода
    TextWatcher mInWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer=null;
            }
            resetDictionary();
            mTranslateOutputTextView.setText("");

        }

        @Override
        public void afterTextChanged(final Editable editable) {
            mTimer = new CountDownTimer(MtimerDelay, MtimerDelay) {//устанавливаем таймер чтобы после ввода каждой буквы не делать перевод
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    if (!TextUtils.isEmpty(editable.toString())) {
                        translateText(editable.toString(), getDirection());// переводим текст
                    }
                }
            };
            mTimer.start();
            changeButtonsShown(editable.toString(), mTranslateOutputTextView.getText().toString());

            if (isFavoritWord) {//меняем "избранность" слова при изменении тескта
                mFavorite.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                isFavoritWord = false;
            }

        }
    };
    TextWatcher mOutWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {//при изменении текста в поле резульата перевода записываем результат в БД и "смотрим" в словарь
            changeButtonsShown(mTranslateInputEditText.getText().toString(), editable.toString());
            addToDb(mTranslateInputEditText.getText().toString().trim(), mTranslateOutputTextView.getText().toString().trim(), getDirection(), false);
            lookInDictionary(mTranslateInputEditText.getText().toString().trim(), getDirection());
        }
    };

//листенер синтезатора речи
    //region vokalizer.listener
    @Override
    public void onSynthesisBegin(Vocalizer vocalizer) {

    }

    @Override
    public void onSynthesisDone(Vocalizer vocalizer, Synthesis synthesis) {

    }

    @Override
    public void onPlayingBegin(Vocalizer vocalizer) {
        if (isSpeakerInput) mInputSpeaker.setImageResource(R.drawable.ic_speaker_yellow_24dp);
        if (isSpeakerOutput) mOututSpeaker.setImageResource(R.drawable.ic_speaker_yellow_24dp);
    }

    @Override
    public void onPlayingDone(Vocalizer vocalizer) {
        resetVocalizer();
        if (isSpeakerInput) {
            mInputSpeaker.setImageResource(R.drawable.ic_speaker_grey_24dp);
            isSpeakerInput = false;
        }
        if (isSpeakerOutput) {
            mOututSpeaker.setImageResource(R.drawable.ic_speaker_grey_24dp);
            isSpeakerOutput = false;
        }
    }

    @Override
    public void onVocalizerError(Vocalizer vocalizer, Error error) {

    }

    //endregion

    //region recognizer.listener
    @Override
    public void onRecordingBegin(Recognizer recognizer) {
        //// TODO: 09.04.2017 сделать интерфейс записи голоса
        Toast.makeText(getActivity(), "Говорите...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSpeechDetected(Recognizer recognizer) {

    }

    @Override
    public void onSpeechEnds(Recognizer recognizer) {

    }

    @Override
    public void onRecordingDone(Recognizer recognizer) {

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
    }

    //endregion

    //метод динамически строит Вью для отображения словаря, не стал использовать RecyclerView потому что список не больше чем на 1,5 экрана и посчитал что динамичеки будет построить легче и красивее
    private void addDictionary(TranslatedWords words) {
        resetDictionary();
        if (words.getDefenitions() != null) {
            lastDictId = words.getId();

            RealmList<Defenition> defList = words.getDefenitions();
            int tr_LastId = -1;//tr - последнее Id переводов
            int defLastId = -1;//def - последнее Id словарных статей
            int ts_LastId = -1;//ts - последнее Id транскрипция
            int numeric_tr_LastId = -1;//ts - массив нумераций занчений
            int meanLastId = -1;//mean - массив занчений
            for (int i = 0; i < defList.size(); i++) {
                //region делаем вьюху для массива словарных статей
                RelativeLayout.LayoutParams params_def = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params_def.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                if (i != 0 && defList.get(i - 1).getTr().get(defList.get(i - 1).getTr().size() - 1).getMean().size() != 0) {
                    params_def.addRule(RelativeLayout.BELOW, meanLastId);
                } else if (tr_LastId > -1) {
                    params_def.addRule(RelativeLayout.BELOW, tr_LastId);
                } else if (defLastId > -1) {
                    params_def.addRule(RelativeLayout.BELOW, defLastId);
                }
                String defString = defList.get(i).getText();
                TextView defTextView = new TextView(getActivity());
                defTextView.setText(defString);
                defTextView.setPadding(0, dpToPx(8), 0, 0);
                defTextView.setTextColor(getResources().getColor(R.color.primary_text));
                defTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
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
                if (defList.get(i).getTranscription() != null) {
                    RelativeLayout.LayoutParams params_ts = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params_ts.addRule(RelativeLayout.RIGHT_OF, defLastId);
                    params_ts.addRule(RelativeLayout.ALIGN_BOTTOM, defLastId);
                    TextView tsTextView = new TextView(getActivity());
                    tsTextView.setText("[" + defList.get(i).getTranscription() + "]");
                    tsTextView.setPadding(dpToPx(8), dpToPx(8), 0, 0);
                    tsTextView.setTextColor(getResources().getColor(R.color.secondary_text));
                    tsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
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
                if (defList.get(i).getPos() != null) {
                    if (ts_LastId != -1) {
                        params_pos.addRule(RelativeLayout.RIGHT_OF, ts_LastId);
                        params_pos.addRule(RelativeLayout.ALIGN_BOTTOM, ts_LastId);
                    } else {
                        params_pos.addRule(RelativeLayout.RIGHT_OF, defLastId);
                        params_pos.addRule(RelativeLayout.ALIGN_BOTTOM, defLastId);
                    }
                    TextView posTextView = new TextView(getActivity());
                    posTextView.setText(Utils.translatePos(defList.get(i).getPos()));
                    posTextView.setPadding(dpToPx(8), dpToPx(8), 0, 0);
                    posTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    posTextView.setTextColor(getResources().getColor(R.color.green_for_def));
                    posTextView.setTypeface(null, Typeface.ITALIC);
                    posTextView.setLayoutParams(params_pos);
                    mDictionaryContainer.addView(posTextView);
                }
                //endregion
                for (int j = 0; j < defList.get(i).getTr().size(); j++) {
                    //region делаем вьюху для нумерации масива перевода
                    if (defList.get(i).getTr().size() > 1) {
                        RelativeLayout.LayoutParams params_numeric_tr = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params_numeric_tr.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        if (j == 0) {
                            params_numeric_tr.addRule(RelativeLayout.BELOW, defLastId);
                        } else {
                            if (defList.get(i).getTr().get(j - 1).getMean().size() != 0) {
                                params_numeric_tr.addRule(RelativeLayout.BELOW, meanLastId);
                            } else
                                params_numeric_tr.addRule(RelativeLayout.BELOW, tr_LastId);
                        }
                        TextView numericTrTextiew = new TextView(getActivity());
                        numericTrTextiew.setText(String.valueOf(j + 1));
                        numericTrTextiew.setTextColor(getActivity().getResources().getColor(R.color.secondary_text));
                        numericTrTextiew.setPadding(0, 0, dpToPx(8), 0);
                        numericTrTextiew.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
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
                    String Tr_and_Syn = defList.get(i).getTr().get(j).getText();
                    if (defList.get(i) != null) {
                        if (defList.get(i).getTr().get(j).getSyn() != null) {
                            for (int syn = 0; syn < defList.get(i).getTr().get(j).getSyn().size(); syn++) {
                                Tr_and_Syn = Tr_and_Syn + ", " + defList.get(i).getTr().get(j).getSyn().get(syn).getText();
                            }
                        }
                    }
                    RelativeLayout.LayoutParams params_tr = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    if (defList.get(i).getTr().size() == 1) {
                        params_tr.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        params_tr.addRule(RelativeLayout.BELOW, defLastId);
                    } else {
                        if (numeric_tr_LastId > -1) {
                            params_tr.addRule(RelativeLayout.RIGHT_OF, numeric_tr_LastId);
                            params_tr.addRule(RelativeLayout.ALIGN_TOP, numeric_tr_LastId);
                        }
                    }

                    TextView tr_TextView = new TextView(getActivity());
                    tr_TextView.setText(Tr_and_Syn);
                    tr_TextView.setTextColor(getResources().getColor(R.color.blue_for_dictionary));
                    tr_TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
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
                    if (defList.get(i).getTr().get(j).getMean().size() != 0) {
                        RelativeLayout.LayoutParams params_mean = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params_mean.addRule(RelativeLayout.ALIGN_LEFT, tr_LastId);
                        params_mean.addRule(RelativeLayout.BELOW, tr_LastId);
                        TextView meanTextView = new TextView(getActivity());
                        String meanString = "";
                        for (int mean = 0; mean < defList.get(i).getTr().get(j).getMean().size(); mean++) {
                            if (mean + 1 != defList.get(i).getTr().get(j).getMean().size()) {
                                meanString += defList.get(i).getTr().get(j).getMean().get(mean).getText() + ", ";
                            } else
                                meanString += defList.get(i).getTr().get(j).getMean().get(mean).getText();
                        }
                        if (!meanString.equals("")) {
                            meanString = "(" + meanString + ")";
                            meanTextView.setText(meanString);
                        }
                        meanTextView.setTextColor(getResources().getColor(R.color.brown_for_means));
                        meanTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        ;
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

}
