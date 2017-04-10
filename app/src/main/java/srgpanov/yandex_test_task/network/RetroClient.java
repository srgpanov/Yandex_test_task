package srgpanov.yandex_test_task.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static srgpanov.yandex_test_task.Utils.ConstantManager.BASE_DICT_URL;
import static srgpanov.yandex_test_task.Utils.ConstantManager.BASE_TRANSLATE_URL;

/**
 * Created by Пан on 29.03.2017.
 */

public class RetroClient {


    public static YandexTranslateApi getYandexTranslateApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_TRANSLATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(YandexTranslateApi.class);
    }
    public static YandexDictApi getYandexDictApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_DICT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(YandexDictApi.class);
    }

}
