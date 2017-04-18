package srgpanov.yandex_test_task.Data.Dictionary;

import io.realm.RealmObject;

/**
 * Created by Пан on 18.04.2017.
 */

public class Synonymous extends RealmObject {
    private Transcript Transcript;

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

    public String getPos() {
        return Pos;
    }

    public void setPos(String pos) {
        Pos = pos;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    private String Text;
    private String Pos;
    private String Gender;
}
