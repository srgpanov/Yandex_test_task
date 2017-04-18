package srgpanov.yandex_test_task.Data.Dictionary;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Пан on 18.04.2017.
 */

public class Transcript extends RealmObject {
    private Defenition Defenition;
    private String Text;
    private String Pos;
    private String Gender;
    private RealmList <Synonymous> Syn;
    private RealmList <Means> Mean;
    private RealmList <Example> Ex;

    public srgpanov.yandex_test_task.Data.Dictionary.Defenition getDefenition() {
        return Defenition;
    }

    public void setDefenition(srgpanov.yandex_test_task.Data.Dictionary.Defenition defenition) {
        Defenition = defenition;
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

    public RealmList<Synonymous> getSyn() {
        return Syn;
    }

    public void setSyn(RealmList<Synonymous> syn) {
        Syn = syn;
    }

    public RealmList<Means> getMean() {
        return Mean;
    }

    public void setMean(RealmList<Means> mean) {
        Mean = mean;
    }

    public RealmList<Example> getEx() {
        return Ex;
    }

    public void setEx(RealmList<Example> ex) {
        Ex = ex;
    }
}
