package srgpanov.yandex_test_task.Data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Пан on 16.04.2017.
 */

public class Langauge extends RealmObject {
    @PrimaryKey
    private int Id;
    private String name;
    private String abbreviation;
    private int lastUsage;

    public Langauge() {
    }

    public Langauge(int id, String name) {
        Id = id;
        this.name = name;
    }

    public Langauge(int id, String name, String abbreviation) {
        Id = id;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void setName(String name) {

        this.name = name;
    }



    public void setId(int id) {
        Id = id;
    }

    public void setLastUsage(int lastUsage) {

        this.lastUsage = lastUsage;
    }

    public int getId() {

        return Id;
    }

    public String getName() {
        return name;
    }

    public int getLastUsage() {
        return lastUsage;
    }
}
