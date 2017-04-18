package srgpanov.yandex_test_task.Data.Dictionary;

import io.realm.RealmList;
import io.realm.RealmObject;
import srgpanov.yandex_test_task.Data.TranslatedWords;

/**
 * Created by Пан on 18.04.2017.
 */

public class Defenition extends RealmObject {
    private TranslatedWords Words;
    private String Text;
    private String Pos;
    private String Transcription;
    private RealmList<Transcript> Tr;

    public String getTranscription() {
        return Transcription;
    }

    public void setTranscription(String transcription) {
        Transcription = transcription;
    }

    public TranslatedWords getWords() {
        return Words;
    }

    public void setWords(TranslatedWords words) {
        Words = words;
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

    public RealmList<Transcript> getTr() {
        return Tr;
    }

    public void setTr(RealmList<Transcript> tr) {
        Tr = tr;
    }
}
