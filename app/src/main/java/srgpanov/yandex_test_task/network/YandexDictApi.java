package srgpanov.yandex_test_task.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import srgpanov.yandex_test_task.network.res.LookUpResponse;

/**
 * Created by Пан on 10.04.2017.
 */
/**
 * Гет запрос для яндекс словаря
 */
public interface YandexDictApi {
    @GET("lookup?")
    Call<LookUpResponse> lookup(@Query("key") String key,  @Query("lang") String lang,@Query("text") String text);
}
