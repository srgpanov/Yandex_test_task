package srgpanov.yandex_test_task.Data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import srgpanov.yandex_test_task.Data.Dictionary.Defenition;

/**
 * Created by Пан on 30.03.2017.
 */
/**
 * Модель для хранения данных истории переводов
 */
public class TranslatedWords extends RealmObject {
    @PrimaryKey
    private int Id;
    private String InputText;
    private String TranslatedText;
    private String DirectionTranslation;
    private boolean Favorits;
    private FavoritsWord mFavoritsWord;
    private RealmList<Defenition> mDefenitions;

    public RealmList<Defenition> getDefenitions() {
        return mDefenitions;
    }

    public void setDefenitions(RealmList<Defenition> defenitions) {
        mDefenitions = defenitions;
    }

    public FavoritsWord getFavoritsWord() {
        return mFavoritsWord;
    }

    public void setFavoritsWord(FavoritsWord favoritsWord) {
        mFavoritsWord = favoritsWord;
    }

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

