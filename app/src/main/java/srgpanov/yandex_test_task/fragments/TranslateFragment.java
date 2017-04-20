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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
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
import srgpanov.yandex_test_task.Data.TranslatedWords;
import srgpanov.yandex_test_task.InputLangActivity;
import srgpanov.yandex_test_task.MainActivity;
import srgpanov.yandex_test_task.R;
import srgpanov.yandex_test_task.Utils.AvailableLanguages;
import srgpanov.yandex_test_task.Utils.ConstantManager;
import srgpanov.yandex_test_task.Utils.Utils;
import srgpanov.yandex_test_task.YandexAplication;
import srgpanov.yandex_test_task.YandexEditText;
import srgpanov.yandex_test_task.network.RetroClient;
import srgpanov.yandex_test_task.network.YandexDictApi;
import srgpanov.yandex_test_task.network.YandexTranslateApi;
import srgpanov.yandex_test_task.network.res.LookUpResponse;
import srgpanov.yandex_test_task.network.res.TranslateResponse;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static srgpanov.yandex_test_task.R.string.en;
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
    private TextView mToolbarLeftTextView;
    private TextView mToolbarRightTextView;
    private ImageView mToolbarImageView;
    private RelativeLayout mTranslateContainer;
    private RelativeLayout mDictionaryContainer;
    private TextView mTolbarLeftTextView;
    private TextView mTolbarRightTextView;


    //endregion
    private SharedPreferences mPreferences;
    private Realm mRealm;
    private CountDownTimer mTimer;
    private int MtimerDelay;
    private Vocalizer mVocalizer;
    private Recognizer mRecognizer;
    private boolean isFavoritWord = false;
    private boolean isSpeakerInput = false;
    private boolean isSpeakerOutput = false;

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
        mTolbarLeftTextView = (TextView) rootView.findViewById(R.id.toolbar_left_txt_view);
        mTolbarRightTextView = (TextView) rootView.findViewById(R.id.toolbar_right_txt_view);
        setupButtons(rootView);
        setupTextViews(rootView);
        setupToolbar(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //добавляем TextWatcher для задержки обработки вводимых данных пользователем
        mTranslateInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                resetVocalizer();
                resetDictionary();
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                mTimer = new CountDownTimer(MtimerDelay, MtimerDelay) {
                    @Override
                    public void onTick(long l) {
                    }

                    @Override
                    public void onFinish() {
                        if (!TextUtils.isEmpty(editable.toString())) {
                            translateText(editable.toString(), getDirection(getActivity()));// перевод текста и запись в базу данных запускается в отдельном потоке
                        }
                    }
                };
                mTimer.start();

                if (isFavoritWord) {
                    mFavorite.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                    isFavoritWord = false;
                }
            }
        });
        mTranslateOutputTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                resetDictionary();
                addToDb(mTranslateInputEditText.getText().toString().trim(), mTranslateOutputTextView.getText().toString().trim(), getDirection(getActivity()), false);
                lookInDictionary(mTranslateInputEditText.getText().toString().trim(), getDirection(getActivity()));

            }

        });
        if (((MainActivity) getActivity()).getWordLastId() != 0) {
            TranslatedWords words = mRealm.where(TranslatedWords.class).equalTo("Id", ((MainActivity) getActivity()).getWordLastId()).findFirst();
            if (words != null) {
                mTranslateInputEditText.setText(words.getInputText());
                mTranslateOutputTextView.setText(words.getTranslatedText());
                addDictionary(words);
            }
        }

    }


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
                String tempLangauge;
                tempLangauge = mToolbarLeftTextView.getText().toString();
                mToolbarLeftTextView.setText(mToolbarRightTextView.getText().toString());
                mToolbarRightTextView.setText(tempLangauge);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(ConstantManager.TOOLBAR_LEFT_TEXT_VIEW, mToolbarLeftTextView.getText().toString());
                editor.putString(ConstantManager.TOOLBAR_RIGHT_TEXT_VIEW, mToolbarRightTextView.getText().toString());
                editor.apply();
                swapTranslatedText();
                resetDictionary();
            }
        });
    }

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


    private void setupTextViews(View rootView) {
        mTranslateOutputTextView = (TextView) rootView.findViewById(R.id.output_txt_view);
        mTranslateInputEditText = (YandexEditText) rootView.findViewById(R.id.input_edit_text);
        if (TextUtils.isEmpty(mTranslateInputEditText.getText().toString())) {
            mTranslateInputEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

    }


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
                    startSpeech(mToolbarLeftTextView.getText().toString(), mTranslateInputEditText.getText().toString());
                    isSpeakerInput = true;
                    mInputSpeaker.setImageResource(R.drawable.ic_speaker_yellow_24dp);
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
                    startSpeech(mToolbarRightTextView.getText().toString(), mTranslateOutputTextView.getText().toString());
                    isSpeakerOutput = true;
                    mOututSpeaker.setImageResource(R.drawable.ic_speaker_yellow_24dp);
                }
            }
        });
        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFavoritWord) {
                    mFavorite.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
                    addToDb(mTranslateInputEditText.getText().toString().trim(), mTranslateOutputTextView.getText().toString().trim(), getDirection(getActivity()), true);
                    isFavoritWord = true;
                } else {
                    mFavorite.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                    deleteFromFavorits(mTranslateInputEditText.getText().toString(), mTranslateOutputTextView.getText().toString());
                    isFavoritWord = false;
                }
            }
        });
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "share", Toast.LENGTH_SHORT).show();
            }
        });
        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("translate",mTranslateOutputTextView.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getActivity(), R.string.copied, Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                                                     word.getHistoryWords().setFavorits(false);
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

    private void startSpeech(String lang, String text) {
        if (!TextUtils.isEmpty(text)) {
            resetVocalizer();
            if (lang.equals(getResources().getString(R.string.ru))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, mPreferences.getString(ConstantManager.SPEECH_VOICE, ConstantManager.SPEECH_VOICE_ZAHAR));
            } else if (lang.equals(getResources().getString(en))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.ENGLISH, text, true, mPreferences.getString(ConstantManager.SPEECH_VOICE, ConstantManager.SPEECH_VOICE_ZAHAR));
            } else if (lang.equals(getResources().getString(R.string.uk))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.UKRAINIAN, text, true, mPreferences.getString(ConstantManager.SPEECH_VOICE, ConstantManager.SPEECH_VOICE_ZAHAR));
            } else if (lang.equals(getResources().getString(R.string.tr))) {
                mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.TURKISH, text, true, mPreferences.getString(ConstantManager.SPEECH_VOICE, ConstantManager.SPEECH_VOICE_ZAHAR));
            }
            mVocalizer.setListener(this);
            mVocalizer.start();
        }
    }

    public void translateText(String text, final String lang) {
        if (Utils.isNetworkAvailable(getActivity())) {
            YandexTranslateApi translateApi = RetroClient.getYandexTranslateApi();
            Call<TranslateResponse> translateResponseCall = translateApi.translateText(ConstantManager.KEY_API_TRANSLATE, text, lang);
            translateResponseCall.enqueue(new Callback<TranslateResponse>() {
                @Override
                public void onResponse(Call<TranslateResponse> call, Response<TranslateResponse> response) {
                    if (response.isSuccessful()) {
                        mTranslateOutputTextView.setText(response.body().getText());
                    }
                }

                @Override
                public void onFailure(Call<TranslateResponse> call, Throwable t) {

                    Toast.makeText(getActivity(), "666", Toast.LENGTH_SHORT).show();
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

    public void lookInDictionary(final String text, final String lang) {
        if (Utils.isNetworkAvailable(getActivity())) {
            if (!Utils.isMoreTwoWords(text) && !TextUtils.isEmpty(text)) {
                YandexDictApi dictApi = RetroClient.getYandexDictApi();
                Call<LookUpResponse> lookUpResponseCall = dictApi.lookup(ConstantManager.KEY_API_DICT, lang, text);
                lookUpResponseCall.enqueue(new Callback<LookUpResponse>() {
                    @Override
                    public void onResponse(Call<LookUpResponse> call, Response<LookUpResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                addDictionaryDb(response.body(), text, lang);

                            }
                        }
                        if (response.code() != 200)
                            //todo обработать ошибки
                            Toast.makeText(getActivity(), "666", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<LookUpResponse> call, Throwable t) {
                        Toast.makeText(getActivity(), "777", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Snackbar.make(mTranslateContainer, R.string.check_intenet_conection, Snackbar.LENGTH_SHORT).show();
        }
    }


    private void addDictionaryDb(final LookUpResponse body, final String text, final String lang) {

        final List<LookUpResponse.Def> defList = body.getDef();
        final Realm newRealm = Realm.getDefaultInstance();
        Realm.Transaction transactionDict = new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TranslatedWords words = realm.where(TranslatedWords.class).equalTo("InputText", text).equalTo("DirectionTranslation", lang).findFirst();
                if (words != null) {
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
                        if (words.getFavoritsWord() != null) {
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
                        addDictionary(words);
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


    private void addToDb(final String inputText, final String translatedText, final String directionTranslate, final boolean isFavorits) {
        if (!TextUtils.isEmpty(inputText)) {
            final Realm newRealm = Realm.getDefaultInstance();
            newRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (isFavorits) {
                        FavoritsWord checkFavWord = realm.where(FavoritsWord.class)
                                .equalTo("InputText", mTranslateInputEditText.getText().toString().trim())
                                .equalTo("TranslatedText", mTranslateOutputTextView.getText().toString().trim())
                                .findFirst();
                        if (checkFavWord == null) {
                            Number currentIdNum = realm.where(FavoritsWord.class).max("Id");
                            int nextId;
                            if (currentIdNum == null) {
                                nextId = 1;
                            } else {
                                nextId = currentIdNum.intValue() + 1;
                            }
                            FavoritsWord favoritsWord = realm.createObject(FavoritsWord.class, nextId);
                            favoritsWord.setInputText(inputText);
                            favoritsWord.setTranslatedText(translatedText);
                            favoritsWord.setDirectionTranslation(directionTranslate);
                            favoritsWord.setFavorits(true);
                        }
                    }
                    TranslatedWords word = realm.where(TranslatedWords.class)
                            .equalTo("InputText", mTranslateInputEditText.getText().toString().trim())
                            .equalTo("TranslatedText", mTranslateOutputTextView.getText().toString().trim())
                            .findFirst();
                    if (word == null) {
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
                        newHistoryWord.setFavorits(isFavorits);
                        realm.copyToRealmOrUpdate(newHistoryWord);
                    } else {
                        if (word.getFavoritsWord() == null && isFavorits) {
                            word.setFavorits(true);
                        }
                        realm.copyToRealmOrUpdate(word);
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getActivity(), "sucses", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).setWordLastId(newRealm.where(TranslatedWords.class).max("Id").intValue());
                    newRealm.close();

                    if (isFavorits) {
                        final Realm newRealm = Realm.getDefaultInstance();
                        newRealm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Number maxFavId = realm.where(FavoritsWord.class).max("Id");
                                Number maxHistoryId = realm.where(TranslatedWords.class).max("Id");
                                FavoritsWord lastFavoritsWord = realm.where(FavoritsWord.class).equalTo("Id", maxFavId.intValue()).findFirst();
                                TranslatedWords lastHistoryWord = realm.where(TranslatedWords.class).equalTo("Id", maxHistoryId.intValue()).findFirst();
                                lastFavoritsWord.setHistoryWords(lastHistoryWord);
                                lastHistoryWord.setFavoritsWord(lastFavoritsWord);
                                if (lastHistoryWord.getDefenitions() != null) {
                                    lastFavoritsWord.setDefenitions(lastHistoryWord.getDefenitions());
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
                        });

                    }

                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Toast.makeText(getActivity(), "ne sucses", Toast.LENGTH_SHORT).show();
                    newRealm.close();
                }
            });
        }
    }

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

    private String getDirection(Context context) {
        String inputLangauge = mTolbarLeftTextView.getText().toString();
        String outputLangauge = mToolbarRightTextView.getText().toString();
        AvailableLanguages availableLanguages = new AvailableLanguages(context);
        return availableLanguages.langaugeToAbbreviations(inputLangauge, outputLangauge);
    }

    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity(), "begin", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSpeechDetected(Recognizer recognizer) {
        Toast.makeText(getActivity(), "detected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSpeechEnds(Recognizer recognizer) {

    }

    @Override
    public void onRecordingDone(Recognizer recognizer) {

        Toast.makeText(getActivity(), "RecordingDone", Toast.LENGTH_SHORT).show();

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
        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
    }

    //endregion
    private void addDictionary(TranslatedWords words) {
        if (words.getDefenitions() != null) {
            RealmList<Defenition> defList = words.getDefenitions();
            int tr_LastId = -1;//tr - массив переводов
            int defLastId = -1;//def - массив словарных статей
            int ts_LastId = -1;//ts - массив транскрипция
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
