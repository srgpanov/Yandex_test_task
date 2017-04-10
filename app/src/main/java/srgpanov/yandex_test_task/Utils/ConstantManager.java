package srgpanov.yandex_test_task.Utils;

/**
 * Created by Пан on 30.03.2017.
 */

public interface ConstantManager {
    String KEY_API_TRANSLATE = "trnsl.1.1.20170328T172834Z.4d83a6640bf059fd.aedc220f03882bdaa7022f861ed4f4f895d1e4be";
    String KEY_API_SPEECHKIT = "4001d129-f936-4796-9609-98280f01e791";
    String KEY_API_DICT = "dict.1.1.20170409T185108Z.79d5015c882fb26d.aca756c619f2b0d51e86ce223f31aa23add5ae9c";
    String BASE_TRANSLATE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";
    String BASE_DICT_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/";
    int CODE_GET_LANG_INPUT = 100;
    int CODE_GET_LANG_OUTPUT = 101;
    int REQUEST_PERMISSION_CODE_RECORD_AUDIO = 102;


    int PERMISSION_REQUEST_SETTINGS_CODE = 103;

}
