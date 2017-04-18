package srgpanov.yandex_test_task.Data.Dictionary;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Пан on 18.04.2017.
 */

public class Example extends RealmObject {
    @PrimaryKey
    private String Text;
    private RealmList<ExTranscript> ExTr;
}
