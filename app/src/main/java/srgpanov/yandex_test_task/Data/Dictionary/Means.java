package srgpanov.yandex_test_task.Data.Dictionary;

import io.realm.RealmObject;

/**
 * Created by Пан on 18.04.2017.
 */

public class Means extends RealmObject {
    private Transcript Transcript;
    private String Text;

    public srgpanov.yandex_test_task.Data.Dictionary.Transcript getTranscript() {
        return Transcript;
    }

    public void setTranscript(srgpanov.yandex_test_task.Data.Dictionary.Transcript transcript) {
        Transcript = transcript;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
