package srgpanov.yandex_test_task.Utils;

import ru.yandex.speechkit.Vocalizer;

/**
 * Created by Пан on 30.03.2017.
 */

public interface ConstantManager {
    String KEY_API_TRANSLATE = "trnsl.1.1.20170328T172834Z.4d83a6640bf059fd.aedc220f03882bdaa7022f861ed4f4f895d1e4be";
    String KEY_API_SPEECHKIT = "4001d129-f936-4796-9609-98280f01e791";
    String KEY_API_DICT = "dict.1.1.20170409T185108Z.79d5015c882fb26d.aca756c619f2b0d51e86ce223f31aa23add5ae9c";
    String BASE_TRANSLATE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";
    String BASE_DICT_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/";
    //todo сделать изменение ключа из настроек

    String FRAGMENT_TRANSLATE = "translate";
    String FRAGMENT_HISTORY = "history";
    String FRAGMENT_FAVORITS = "favorits";
    String FRAGMENT_SETTING = "setting";

    String TOOLBAR_LEFT_TEXT_VIEW = "lefttxtview";
    String TOOLBAR_RIGHT_TEXT_VIEW = "righttxtview";
    String INPUT_TEXT_VIEW = "inputtxtview";
    String OUTPUT_TEXT_VIEW = "outputtxtview";

    String SPEECH_VOICE = "Voice";

    String SPEECH_VOICE_ZAHAR = Vocalizer.Voice.ZAHAR;
    String SPEECH_VOICE_ALYSS = Vocalizer.Voice.ALYSS;
    String SPEECH_VOICE_ERMIL = Vocalizer.Voice.ERMIL;
    String SPEECH_VOICE_JANE = Vocalizer.Voice.JANE;
    String SPEECH_VOICE_OMAZH = Vocalizer.Voice.OMAZH;

    String DELAY_TO_TRANSLATE = "delay";

    int DELAY_LOW =500;
    int DELAY_NORMAL=2000;
    int DELAY_HIGHT=5000;

    int CODE_GET_LANG_INPUT = 100;
    int CODE_GET_LANG_OUTPUT = 101;
    int REQUEST_PERMISSION_CODE_RECORD_AUDIO = 102;
    int PERMISSION_REQUEST_SETTINGS_CODE = 103;
    int CODE_DELETE_HISTORY =104;
    int CODE_DELETE_FAVORITS = 105;


    String CURRENT_FRAGMENT ="current_fragment" ;
    String LAST_DICT_ID = "last_word";
    String TAG_DELETE_HISTORY ="deleteHistory" ;
    String TAG_DELETE_FAVORITS ="deleteFavorits" ;
    String SORTING_HISTORY="sortingHistory";
    String SORTING_FAVORITS="sortingFavorits";
    String LAST_LANG = "lastLang";


    String LAST_INLANG ="lastInLang" ;
    String LAST_OUTLANG="lastOutLang";
}
