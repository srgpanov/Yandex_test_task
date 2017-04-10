package srgpanov.yandex_test_task.Data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Пан on 30.03.2017.
 */

public class TranslatedWords extends RealmObject {
    @PrimaryKey
    private int Id;
    private String InputText;
    private String TranslatedText;
    private String DirectionTranslation;
    private boolean Favorits;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getInputText() {
        return InputText;
    }

    public void setInputText(String inputText) {
        InputText = inputText;
    }

    public String getTranslatedText() {
        return TranslatedText;
    }

    public void setTranslatedText(String translatedText) {
        TranslatedText = translatedText;
    }

    public String getDirectionTranslation() {
        return DirectionTranslation;
    }

    public void setDirectionTranslation(String directionTranslation) {
        DirectionTranslation = directionTranslation;
    }

    public boolean isFavorits() {
        return Favorits;
    }

    public void setFavorits(boolean favorits) {
        Favorits = favorits;
    }
}

