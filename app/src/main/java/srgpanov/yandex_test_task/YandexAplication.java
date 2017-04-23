package srgpanov.yandex_test_task;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.yandex.speechkit.SpeechKit;
import srgpanov.yandex_test_task.Data.Langauge;

import static srgpanov.yandex_test_task.Utils.ConstantManager.KEY_API_SPEECHKIT;

/**
 * Created by Пан on 11.04.2017.
 */

public class YandexAplication extends Application {
    static SharedPreferences mPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean hasVisited = mPreferences.getBoolean("hasVisited", false);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        SpeechKit.getInstance().configure(getApplicationContext(),KEY_API_SPEECHKIT);

        if (!hasVisited) {
            initLangauges();
            SharedPreferences.Editor e = mPreferences.edit();
            e.putBoolean("hasVisited", true);
            e.commit();
        }

    }

//при вервом запуске приложения иницилизируем  доступные языки
    private void initLangauges() {
        Realm realm = Realm.getDefaultInstance();
        Langauge langauge0 = new Langauge(0, getApplicationContext().getString(R.string.af), "af");
        Langauge langauge1 = new Langauge(1, getApplicationContext().getString(R.string.am), "am");
        Langauge langauge2 = new Langauge(2, getApplicationContext().getString(R.string.ar), "ar");
        Langauge langauge3 = new Langauge(3, getApplicationContext().getString(R.string.az), "az");
        Langauge langauge4 = new Langauge(4, getApplicationContext().getString(R.string.ba), "ba");
        Langauge langauge5 = new Langauge(5, getApplicationContext().getString(R.string.be), "be");
        Langauge langauge6 = new Langauge(6, getApplicationContext().getString(R.string.bg), "bg");
        Langauge langauge7 = new Langauge(7, getApplicationContext().getString(R.string.bn), "bn");
        Langauge langauge8 = new Langauge(8, getApplicationContext().getString(R.string.bs), "bs");
        Langauge langauge9 = new Langauge(9, getApplicationContext().getString(R.string.ca), "ca");
        Langauge langauge10 = new Langauge(10, getApplicationContext().getString(R.string.ceb), "ceb");
        Langauge langauge11 = new Langauge(11, getApplicationContext().getString(R.string.cs), "cs");
        Langauge langauge12 = new Langauge(12, getApplicationContext().getString(R.string.cy), "cy");
        Langauge langauge13 = new Langauge(13, getApplicationContext().getString(R.string.da), "da");
        Langauge langauge14 = new Langauge(14, getApplicationContext().getString(R.string.de), "de");
        Langauge langauge15 = new Langauge(15, getApplicationContext().getString(R.string.el), "el");
        Langauge langauge16 = new Langauge(16, getApplicationContext().getString(R.string.en), "en");
        Langauge langauge17 = new Langauge(17, getApplicationContext().getString(R.string.eo), "eo");
        Langauge langauge18 = new Langauge(18, getApplicationContext().getString(R.string.es), "es");
        Langauge langauge19 = new Langauge(19, getApplicationContext().getString(R.string.et), "et");
        Langauge langauge20 = new Langauge(20, getApplicationContext().getString(R.string.eu), "eu");
        Langauge langauge21 = new Langauge(21, getApplicationContext().getString(R.string.fa), "fa");
        Langauge langauge22 = new Langauge(22, getApplicationContext().getString(R.string.fi), "fi");
        Langauge langauge23 = new Langauge(23, getApplicationContext().getString(R.string.fr), "fr");
        Langauge langauge24 = new Langauge(24, getApplicationContext().getString(R.string.gd), "gd");
        Langauge langauge25 = new Langauge(25, getApplicationContext().getString(R.string.gl), "gl");
        Langauge langauge26 = new Langauge(26, getApplicationContext().getString(R.string.gu), "gu");
        Langauge langauge27 = new Langauge(27, getApplicationContext().getString(R.string.he), "he");
        Langauge langauge28 = new Langauge(28, getApplicationContext().getString(R.string.hi), "hi");
        Langauge langauge29 = new Langauge(29, getApplicationContext().getString(R.string.hr), "hr");
        Langauge langauge30 = new Langauge(30, getApplicationContext().getString(R.string.ht), "ht");
        Langauge langauge31 = new Langauge(31, getApplicationContext().getString(R.string.hu), "hu");
        Langauge langauge32 = new Langauge(32, getApplicationContext().getString(R.string.hy), "hy");
        Langauge langauge33 = new Langauge(33, getApplicationContext().getString(R.string.id), "id");
        Langauge langauge34 = new Langauge(34, getApplicationContext().getString(R.string.is), "is");
        Langauge langauge35 = new Langauge(35, getApplicationContext().getString(R.string.it), "it");
        Langauge langauge36 = new Langauge(36, getApplicationContext().getString(R.string.ja), "ja");
        Langauge langauge37 = new Langauge(37, getApplicationContext().getString(R.string.jv), "jv");
        Langauge langauge38 = new Langauge(38, getApplicationContext().getString(R.string.ka), "ka");
        Langauge langauge39 = new Langauge(39, getApplicationContext().getString(R.string.kk), "kk");
        Langauge langauge40 = new Langauge(40, getApplicationContext().getString(R.string.km), "km");
        Langauge langauge41 = new Langauge(41, getApplicationContext().getString(R.string.kn), "kn");
        Langauge langauge42 = new Langauge(42, getApplicationContext().getString(R.string.ko), "ko");
        Langauge langauge43 = new Langauge(43, getApplicationContext().getString(R.string.ky), "ky");
        Langauge langauge44 = new Langauge(44, getApplicationContext().getString(R.string.la), "la");
        Langauge langauge45 = new Langauge(45, getApplicationContext().getString(R.string.lb), "lb");
        Langauge langauge46 = new Langauge(46, getApplicationContext().getString(R.string.lo), "lo");
        Langauge langauge47 = new Langauge(47, getApplicationContext().getString(R.string.lt), "lt");
        Langauge langauge48 = new Langauge(48, getApplicationContext().getString(R.string.lv), "lv");
        Langauge langauge49 = new Langauge(49, getApplicationContext().getString(R.string.mg), "mg");
        Langauge langauge50 = new Langauge(50, getApplicationContext().getString(R.string.mhr), "mhr");
        Langauge langauge51 = new Langauge(51, getApplicationContext().getString(R.string.mi), "mi");
        Langauge langauge52 = new Langauge(52, getApplicationContext().getString(R.string.mk), "mk");
        Langauge langauge53 = new Langauge(53, getApplicationContext().getString(R.string.ml), "ml");
        Langauge langauge54 = new Langauge(54, getApplicationContext().getString(R.string.mn), "mn");
        Langauge langauge55 = new Langauge(55, getApplicationContext().getString(R.string.mr), "mr");
        Langauge langauge56 = new Langauge(56, getApplicationContext().getString(R.string.mrj), "mrj");
        Langauge langauge57 = new Langauge(57, getApplicationContext().getString(R.string.ms), "ms");
        Langauge langauge58 = new Langauge(58, getApplicationContext().getString(R.string.mt), "mt");
        Langauge langauge59 = new Langauge(59, getApplicationContext().getString(R.string.my), "my");
        Langauge langauge60 = new Langauge(60, getApplicationContext().getString(R.string.ne), "ne");
        Langauge langauge61 = new Langauge(61, getApplicationContext().getString(R.string.nl), "nl");
        Langauge langauge62 = new Langauge(62, getApplicationContext().getString(R.string.no), "no");
        Langauge langauge63 = new Langauge(63, getApplicationContext().getString(R.string.pa), "pa");
        Langauge langauge64 = new Langauge(64, getApplicationContext().getString(R.string.pap), "pap");
        Langauge langauge65 = new Langauge(65, getApplicationContext().getString(R.string.pl), "pl");
        Langauge langauge66 = new Langauge(66, getApplicationContext().getString(R.string.pt), "pt");
        Langauge langauge67 = new Langauge(67, getApplicationContext().getString(R.string.ro), "ro");
        Langauge langauge68 = new Langauge(68, getApplicationContext().getString(R.string.ru), "ru");
        Langauge langauge69 = new Langauge(69, getApplicationContext().getString(R.string.si), "si");
        Langauge langauge70 = new Langauge(70, getApplicationContext().getString(R.string.sk), "sk");
        Langauge langauge71 = new Langauge(71, getApplicationContext().getString(R.string.sl), "sl");
        Langauge langauge72 = new Langauge(72, getApplicationContext().getString(R.string.sq), "sq");
        Langauge langauge73 = new Langauge(73, getApplicationContext().getString(R.string.sr), "sr");
        Langauge langauge74 = new Langauge(74, getApplicationContext().getString(R.string.su), "su");
        Langauge langauge75 = new Langauge(75, getApplicationContext().getString(R.string.sv), "sv");
        Langauge langauge76 = new Langauge(76, getApplicationContext().getString(R.string.sw), "sw");
        Langauge langauge77 = new Langauge(77, getApplicationContext().getString(R.string.ta), "ta");
        Langauge langauge78 = new Langauge(78, getApplicationContext().getString(R.string.te), "te");
        Langauge langauge79 = new Langauge(79, getApplicationContext().getString(R.string.tg), "tg");
        Langauge langauge80 = new Langauge(80, getApplicationContext().getString(R.string.th), "th");
        Langauge langauge81 = new Langauge(81, getApplicationContext().getString(R.string.tl), "tl");
        Langauge langauge82 = new Langauge(82, getApplicationContext().getString(R.string.tr), "tr");
        Langauge langauge83 = new Langauge(83, getApplicationContext().getString(R.string.tt), "tt");
        Langauge langauge84 = new Langauge(84, getApplicationContext().getString(R.string.udm), "udm");
        Langauge langauge85 = new Langauge(85, getApplicationContext().getString(R.string.uk), "uk");
        Langauge langauge86 = new Langauge(86, getApplicationContext().getString(R.string.ur), "ur");
        Langauge langauge87 = new Langauge(87, getApplicationContext().getString(R.string.uz), "uz");
        Langauge langauge88 = new Langauge(88, getApplicationContext().getString(R.string.vi), "vi");
        Langauge langauge89 = new Langauge(89, getApplicationContext().getString(R.string.xh), "xh");
        Langauge langauge90 = new Langauge(90, getApplicationContext().getString(R.string.yi), "yi");
        Langauge langauge91 = new Langauge(91, getApplicationContext().getString(R.string.zh), "zh");
        realm.beginTransaction();
        Langauge newLangauge0  = realm.copyToRealm(langauge0 );
        Langauge newLangauge1  = realm.copyToRealm(langauge1 );
        Langauge newLangauge2  = realm.copyToRealm(langauge2 );
        Langauge newLangauge3  = realm.copyToRealm(langauge3 );
        Langauge newLangauge4  = realm.copyToRealm(langauge4 );
        Langauge newLangauge5  = realm.copyToRealm(langauge5 );
        Langauge newLangauge6  = realm.copyToRealm(langauge6 );
        Langauge newLangauge7  = realm.copyToRealm(langauge7 );
        Langauge newLangauge8  = realm.copyToRealm(langauge8 );
        Langauge newLangauge9  = realm.copyToRealm(langauge9 );
        Langauge newLangauge10 = realm.copyToRealm(langauge10);
        Langauge newLangauge11 = realm.copyToRealm(langauge11);
        Langauge newLangauge12 = realm.copyToRealm(langauge12);
        Langauge newLangauge13 = realm.copyToRealm(langauge13);
        Langauge newLangauge14 = realm.copyToRealm(langauge14);
        Langauge newLangauge15 = realm.copyToRealm(langauge15);
        Langauge newLangauge16 = realm.copyToRealm(langauge16);
        Langauge newLangauge17 = realm.copyToRealm(langauge17);
        Langauge newLangauge18 = realm.copyToRealm(langauge18);
        Langauge newLangauge19 = realm.copyToRealm(langauge19);
        Langauge newLangauge20 = realm.copyToRealm(langauge20);
        Langauge newLangauge21 = realm.copyToRealm(langauge21);
        Langauge newLangauge22 = realm.copyToRealm(langauge22);
        Langauge newLangauge23 = realm.copyToRealm(langauge23);
        Langauge newLangauge24 = realm.copyToRealm(langauge24);
        Langauge newLangauge25 = realm.copyToRealm(langauge25);
        Langauge newLangauge26 = realm.copyToRealm(langauge26);
        Langauge newLangauge27 = realm.copyToRealm(langauge27);
        Langauge newLangauge28 = realm.copyToRealm(langauge28);
        Langauge newLangauge29 = realm.copyToRealm(langauge29);
        Langauge newLangauge30 = realm.copyToRealm(langauge30);
        Langauge newLangauge31 = realm.copyToRealm(langauge31);
        Langauge newLangauge32 = realm.copyToRealm(langauge32);
        Langauge newLangauge33 = realm.copyToRealm(langauge33);
        Langauge newLangauge34 = realm.copyToRealm(langauge34);
        Langauge newLangauge35 = realm.copyToRealm(langauge35);
        Langauge newLangauge36 = realm.copyToRealm(langauge36);
        Langauge newLangauge37 = realm.copyToRealm(langauge37);
        Langauge newLangauge38 = realm.copyToRealm(langauge38);
        Langauge newLangauge39 = realm.copyToRealm(langauge39);
        Langauge newLangauge40 = realm.copyToRealm(langauge40);
        Langauge newLangauge41 = realm.copyToRealm(langauge41);
        Langauge newLangauge42 = realm.copyToRealm(langauge42);
        Langauge newLangauge43 = realm.copyToRealm(langauge43);
        Langauge newLangauge44 = realm.copyToRealm(langauge44);
        Langauge newLangauge45 = realm.copyToRealm(langauge45);
        Langauge newLangauge46 = realm.copyToRealm(langauge46);
        Langauge newLangauge47 = realm.copyToRealm(langauge47);
        Langauge newLangauge48 = realm.copyToRealm(langauge48);
        Langauge newLangauge49 = realm.copyToRealm(langauge49);
        Langauge newLangauge50 = realm.copyToRealm(langauge50);
        Langauge newLangauge51 = realm.copyToRealm(langauge51);
        Langauge newLangauge52 = realm.copyToRealm(langauge52);
        Langauge newLangauge53 = realm.copyToRealm(langauge53);
        Langauge newLangauge54 = realm.copyToRealm(langauge54);
        Langauge newLangauge55 = realm.copyToRealm(langauge55);
        Langauge newLangauge56 = realm.copyToRealm(langauge56);
        Langauge newLangauge57 = realm.copyToRealm(langauge57);
        Langauge newLangauge58 = realm.copyToRealm(langauge58);
        Langauge newLangauge59 = realm.copyToRealm(langauge59);
        Langauge newLangauge60 = realm.copyToRealm(langauge60);
        Langauge newLangauge61 = realm.copyToRealm(langauge61);
        Langauge newLangauge62 = realm.copyToRealm(langauge62);
        Langauge newLangauge63 = realm.copyToRealm(langauge63);
        Langauge newLangauge64 = realm.copyToRealm(langauge64);
        Langauge newLangauge65 = realm.copyToRealm(langauge65);
        Langauge newLangauge66 = realm.copyToRealm(langauge66);
        Langauge newLangauge67 = realm.copyToRealm(langauge67);
        Langauge newLangauge68 = realm.copyToRealm(langauge68);
        Langauge newLangauge69 = realm.copyToRealm(langauge69);
        Langauge newLangauge70 = realm.copyToRealm(langauge70);
        Langauge newLangauge71 = realm.copyToRealm(langauge71);
        Langauge newLangauge72 = realm.copyToRealm(langauge72);
        Langauge newLangauge73 = realm.copyToRealm(langauge73);
        Langauge newLangauge74 = realm.copyToRealm(langauge74);
        Langauge newLangauge75 = realm.copyToRealm(langauge75);
        Langauge newLangauge76 = realm.copyToRealm(langauge76);
        Langauge newLangauge77 = realm.copyToRealm(langauge77);
        Langauge newLangauge78 = realm.copyToRealm(langauge78);
        Langauge newLangauge79 = realm.copyToRealm(langauge79);
        Langauge newLangauge80 = realm.copyToRealm(langauge80);
        Langauge newLangauge81 = realm.copyToRealm(langauge81);
        Langauge newLangauge82 = realm.copyToRealm(langauge82);
        Langauge newLangauge83 = realm.copyToRealm(langauge83);
        Langauge newLangauge84 = realm.copyToRealm(langauge84);
        Langauge newLangauge85 = realm.copyToRealm(langauge85);
        Langauge newLangauge86 = realm.copyToRealm(langauge86);
        Langauge newLangauge87 = realm.copyToRealm(langauge87);
        Langauge newLangauge88 = realm.copyToRealm(langauge88);
        Langauge newLangauge89 = realm.copyToRealm(langauge89);
        Langauge newLangauge90 = realm.copyToRealm(langauge90);
        Langauge newLangauge91 = realm.copyToRealm(langauge91);
        realm.commitTransaction();


    }

    static public SharedPreferences getPreferences() {
        return mPreferences;
    }

}
