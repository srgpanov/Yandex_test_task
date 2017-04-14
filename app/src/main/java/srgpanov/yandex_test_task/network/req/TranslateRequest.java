package srgpanov.yandex_test_task.network.req;

/**
 * Created by Пан on 28.03.2017.
 */

public class TranslateRequest {
    private String text;
    private String lang;
    private int  options;

    public TranslateRequest(String text, String lang) {
        this.text = text;
        this.lang = lang;
    }

    public TranslateRequest(String text, String lang, int options) {

        this.text = text;
        this.lang = lang;
        this.options = options;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
