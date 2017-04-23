package srgpanov.yandex_test_task.network;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import srgpanov.yandex_test_task.network.res.TranslateResponse;

/**
 * Created by Пан on 28.03.2017.
 */
/**
 * ПОСТ запрос для перевода
 */
public interface YandexTranslateApi {

    @POST("translate?")
    Call<TranslateResponse> translateText(@Query("key") String key, @Query("text") String text, @Query("lang") String lang);
}
