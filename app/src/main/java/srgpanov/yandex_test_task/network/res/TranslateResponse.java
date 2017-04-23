package srgpanov.yandex_test_task.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Пан on 28.03.2017.
 */
/**
 * ответ который приходит от АПИ переводчика
 */
public class TranslateResponse {
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("detected")
    @Expose
    private Detected detected;
    @SerializedName("lang")
    @Expose
    private String lang;

    public String getText() {
        String convertedText = "";
        for (String e : text) {
            convertedText += e;
        }
        return convertedText;
    }

    @SerializedName("text")
    @Expose
    private List<String> text = null;

    public class Detected {
        @SerializedName("lang")
        @Expose
        private String lang;
    }
}

