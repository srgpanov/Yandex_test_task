package srgpanov.yandex_test_task.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Пан on 10.04.2017.
 */

public class Utils {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
    public static boolean isMoreTwoWords(String text){
        boolean truth =false;
        List<String> strings=Arrays.asList(text.trim().split(" "));
        if(strings.size()>2)truth =true;
        return truth;
    }
    public static String translatePos(String text){
        switch (text){
            case "noun": return "сущ";
            case "verb": return "гл";
            case "adjective": return "прил";
            case "adverb": return "нареч";
            case "pronoun": return "мест";
            case "preposition": return "предл";
            case "conjunction": return "союз";
            case "interjection": return "межд";
            case "participle": return "прич";
            case "adverbial participle": return "дееприч";

            default:return text;
        }
    }
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
    }

}
