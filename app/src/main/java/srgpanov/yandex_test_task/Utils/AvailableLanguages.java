package srgpanov.yandex_test_task.Utils;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

import srgpanov.yandex_test_task.R;

/**
 * Created by Пан on 30.03.2017.
 */

public class AvailableLanguages {
    private ArrayMap<String, Integer> mLangIndex = new ArrayMap<>();
    private SparseArray<String> mIndexAbr = new SparseArray<>();
    private SparseArray<String> mIndexLang = new SparseArray<>();
    private Context mContext;

    public AvailableLanguages(Context context) {
        mContext = context;
        initLangIndex();
        initIndexAbr();
        initIndexLang();
    }

    public ArrayMap<String, Integer> getLangIndex() {
        return mLangIndex;

    }

    public SparseArray<String> getIndexAbr() {
        return mIndexAbr;

    }
    public String langaugeToAbbreviations(String input, String output) {
        ArrayMap<String, Integer> indexLangMap = this.getLangIndex();
        SparseArray<String> indexAbrMap = this.getIndexAbr();

        int keyInput = indexLangMap.get(input);
        int keyOutput = indexLangMap.get(output);
        return indexAbrMap.get(keyInput) + "-" + indexAbrMap.get(keyOutput);
    }

    public SparseArray<String> getIndexLang() {
        return mIndexLang;
    }

    private void initIndexAbr() {
        mIndexAbr.put( 1,"af");
        mIndexAbr.put( 2,"am");
        mIndexAbr.put( 3,"ar");
        mIndexAbr.put( 4,"az");
        mIndexAbr.put( 5,"ba");
        mIndexAbr.put( 6,"be");
        mIndexAbr.put( 7,"bg");
        mIndexAbr.put( 8,"bn");
        mIndexAbr.put( 9,"bs");
        mIndexAbr.put(10,"ca");
        mIndexAbr.put(11,"ceb");
        mIndexAbr.put(12,"cs");
        mIndexAbr.put(13,"cy");
        mIndexAbr.put(14,"da");
        mIndexAbr.put(15,"de");
        mIndexAbr.put(16,"el");
        mIndexAbr.put(17,"en");
        mIndexAbr.put(18,"eo");
        mIndexAbr.put(19,"es");
        mIndexAbr.put(20,"et");
        mIndexAbr.put(21,"eu");
        mIndexAbr.put(22,"fa");
        mIndexAbr.put(23,"fi");
        mIndexAbr.put(24,"fr");
        mIndexAbr.put(25,"gd");
        mIndexAbr.put(26,"gl");
        mIndexAbr.put(27,"gu");
        mIndexAbr.put(28,"he");
        mIndexAbr.put(29,"hi");
        mIndexAbr.put(30,"hr");
        mIndexAbr.put(31,"ht");
        mIndexAbr.put(32,"hu");
        mIndexAbr.put(33,"hy");
        mIndexAbr.put(34,"id");
        mIndexAbr.put(35,"is");
        mIndexAbr.put(36,"it");
        mIndexAbr.put(37,"ja");
        mIndexAbr.put(38,"jv");
        mIndexAbr.put(39,"ka");
        mIndexAbr.put(40,"kk");
        mIndexAbr.put(41,"km");
        mIndexAbr.put(42,"kn");
        mIndexAbr.put(43,"ko");
        mIndexAbr.put(44,"ky");
        mIndexAbr.put(45,"la");
        mIndexAbr.put(46,"lb");
        mIndexAbr.put(47,"lo");
        mIndexAbr.put(48,"lt");
        mIndexAbr.put(49,"lv");
        mIndexAbr.put(50,"mg");
        mIndexAbr.put(51,"mhr");
        mIndexAbr.put(52,"mi");
        mIndexAbr.put(53,"mk");
        mIndexAbr.put(54,"ml");
        mIndexAbr.put(55,"mn");
        mIndexAbr.put(56,"mr");
        mIndexAbr.put(57,"mrj");
        mIndexAbr.put(58,"ms");
        mIndexAbr.put(59,"mt");
        mIndexAbr.put(60,"my");
        mIndexAbr.put(61,"ne");
        mIndexAbr.put(62,"nl");
        mIndexAbr.put(63,"no");
        mIndexAbr.put(64,"pa");
        mIndexAbr.put(65,"pap");
        mIndexAbr.put(66,"pl");
        mIndexAbr.put(67,"pt");
        mIndexAbr.put(68,"ro");
        mIndexAbr.put(69,"ru");
        mIndexAbr.put(70,"si");
        mIndexAbr.put(71,"sk");
        mIndexAbr.put(72,"sl");
        mIndexAbr.put(73,"sq");
        mIndexAbr.put(74,"sr");
        mIndexAbr.put(75,"su");
        mIndexAbr.put(76,"sv");
        mIndexAbr.put(77,"sw");
        mIndexAbr.put(78,"ta");
        mIndexAbr.put(79,"te");
        mIndexAbr.put(80,"tg");
        mIndexAbr.put(81,"th");
        mIndexAbr.put(82,"tl");
        mIndexAbr.put(83,"tr");
        mIndexAbr.put(84,"tt");
        mIndexAbr.put(85,"udm");
        mIndexAbr.put(86,"uk");
        mIndexAbr.put(87,"ur");
        mIndexAbr.put(88,"uz");
        mIndexAbr.put(89,"vi");
        mIndexAbr.put(90,"xh");
        mIndexAbr.put(91,"yi");
        mIndexAbr.put(92,"zh");
    }

    private void initLangIndex() {
        mLangIndex.put(mContext.getString(R.string.af), 1);
        mLangIndex.put(mContext.getString(R.string.am), 2);
        mLangIndex.put(mContext.getString(R.string.ar), 3);
        mLangIndex.put(mContext.getString(R.string.az), 4);
        mLangIndex.put(mContext.getString(R.string.ba), 5);
        mLangIndex.put(mContext.getString(R.string.be), 6);
        mLangIndex.put(mContext.getString(R.string.bg), 7);
        mLangIndex.put(mContext.getString(R.string.bn), 8);
        mLangIndex.put(mContext.getString(R.string.bs), 9);
        mLangIndex.put(mContext.getString(R.string.ca), 10);
        mLangIndex.put(mContext.getString(R.string.ceb), 11);
        mLangIndex.put(mContext.getString(R.string.cs), 12);
        mLangIndex.put(mContext.getString(R.string.cy), 13);
        mLangIndex.put(mContext.getString(R.string.da), 14);
        mLangIndex.put(mContext.getString(R.string.de), 15);
        mLangIndex.put(mContext.getString(R.string.el), 16);
        mLangIndex.put(mContext.getString(R.string.en), 17);
        mLangIndex.put(mContext.getString(R.string.eo), 18);
        mLangIndex.put(mContext.getString(R.string.es), 19);
        mLangIndex.put(mContext.getString(R.string.et), 20);
        mLangIndex.put(mContext.getString(R.string.eu), 21);
        mLangIndex.put(mContext.getString(R.string.fa), 22);
        mLangIndex.put(mContext.getString(R.string.fi), 23);
        mLangIndex.put(mContext.getString(R.string.fr), 24);
        mLangIndex.put(mContext.getString(R.string.gd), 25);
        mLangIndex.put(mContext.getString(R.string.gl), 26);
        mLangIndex.put(mContext.getString(R.string.gu), 27);
        mLangIndex.put(mContext.getString(R.string.he), 28);
        mLangIndex.put(mContext.getString(R.string.hi), 29);
        mLangIndex.put(mContext.getString(R.string.hr), 30);
        mLangIndex.put(mContext.getString(R.string.ht), 31);
        mLangIndex.put(mContext.getString(R.string.hu), 32);
        mLangIndex.put(mContext.getString(R.string.hy), 33);
        mLangIndex.put(mContext.getString(R.string.id), 34);
        mLangIndex.put(mContext.getString(R.string.is), 35);
        mLangIndex.put(mContext.getString(R.string.it), 36);
        mLangIndex.put(mContext.getString(R.string.ja), 37);
        mLangIndex.put(mContext.getString(R.string.jv), 38);
        mLangIndex.put(mContext.getString(R.string.ka), 39);
        mLangIndex.put(mContext.getString(R.string.kk), 40);
        mLangIndex.put(mContext.getString(R.string.km), 41);
        mLangIndex.put(mContext.getString(R.string.kn), 42);
        mLangIndex.put(mContext.getString(R.string.ko), 43);
        mLangIndex.put(mContext.getString(R.string.ky), 44);
        mLangIndex.put(mContext.getString(R.string.la), 45);
        mLangIndex.put(mContext.getString(R.string.lb), 46);
        mLangIndex.put(mContext.getString(R.string.lo), 47);
        mLangIndex.put(mContext.getString(R.string.lt), 48);
        mLangIndex.put(mContext.getString(R.string.lv), 49);
        mLangIndex.put(mContext.getString(R.string.mg), 50);
        mLangIndex.put(mContext.getString(R.string.mhr), 51);
        mLangIndex.put(mContext.getString(R.string.mi), 52);
        mLangIndex.put(mContext.getString(R.string.mk), 53);
        mLangIndex.put(mContext.getString(R.string.ml), 54);
        mLangIndex.put(mContext.getString(R.string.mn), 55);
        mLangIndex.put(mContext.getString(R.string.mr), 56);
        mLangIndex.put(mContext.getString(R.string.mrj), 57);
        mLangIndex.put(mContext.getString(R.string.ms), 58);
        mLangIndex.put(mContext.getString(R.string.mt), 59);
        mLangIndex.put(mContext.getString(R.string.my), 60);
        mLangIndex.put(mContext.getString(R.string.ne), 61);
        mLangIndex.put(mContext.getString(R.string.nl), 62);
        mLangIndex.put(mContext.getString(R.string.no), 63);
        mLangIndex.put(mContext.getString(R.string.pa), 64);
        mLangIndex.put(mContext.getString(R.string.pap), 65);
        mLangIndex.put(mContext.getString(R.string.pl), 66);
        mLangIndex.put(mContext.getString(R.string.pt), 67);
        mLangIndex.put(mContext.getString(R.string.ro), 68);
        mLangIndex.put(mContext.getString(R.string.ru), 69);
        mLangIndex.put(mContext.getString(R.string.si), 70);
        mLangIndex.put(mContext.getString(R.string.sk), 71);
        mLangIndex.put(mContext.getString(R.string.sl), 72);
        mLangIndex.put(mContext.getString(R.string.sq), 73);
        mLangIndex.put(mContext.getString(R.string.sr), 74);
        mLangIndex.put(mContext.getString(R.string.su), 75);
        mLangIndex.put(mContext.getString(R.string.sv), 76);
        mLangIndex.put(mContext.getString(R.string.sw), 77);
        mLangIndex.put(mContext.getString(R.string.ta), 78);
        mLangIndex.put(mContext.getString(R.string.te), 79);
        mLangIndex.put(mContext.getString(R.string.tg), 80);
        mLangIndex.put(mContext.getString(R.string.th), 81);
        mLangIndex.put(mContext.getString(R.string.tl), 82);
        mLangIndex.put(mContext.getString(R.string.tr), 83);
        mLangIndex.put(mContext.getString(R.string.tt), 84);
        mLangIndex.put(mContext.getString(R.string.udm), 85);
        mLangIndex.put(mContext.getString(R.string.uk), 86);
        mLangIndex.put(mContext.getString(R.string.ur), 87);
        mLangIndex.put(mContext.getString(R.string.uz), 88);
        mLangIndex.put(mContext.getString(R.string.vi), 89);
        mLangIndex.put(mContext.getString(R.string.xh), 90);
        mLangIndex.put(mContext.getString(R.string.yi), 91);
        mLangIndex.put(mContext.getString(R.string.zh), 92);
    }

    private void initIndexLang() {
        mIndexLang.put(1,mContext.getString(R.string.af));
        mIndexLang.put(2,mContext.getString(R.string.am));
        mIndexLang.put(3,mContext.getString(R.string.ar));
        mIndexLang.put(4,mContext.getString(R.string.az));
        mIndexLang.put(5,mContext.getString(R.string.ba));
        mIndexLang.put(6,mContext.getString(R.string.be));
        mIndexLang.put(7,mContext.getString(R.string.bg));
        mIndexLang.put(8,mContext.getString(R.string.bn));
        mIndexLang.put(9,mContext.getString(R.string.bs));
        mIndexLang.put(10,mContext.getString(R.string.ca));
        mIndexLang.put(11,mContext.getString(R.string.ceb));
        mIndexLang.put(12,mContext.getString(R.string.cs));
        mIndexLang.put(13,mContext.getString(R.string.cy));
        mIndexLang.put(14,mContext.getString(R.string.da));
        mIndexLang.put(15,mContext.getString(R.string.de));
        mIndexLang.put(16,mContext.getString(R.string.el));
        mIndexLang.put(17,mContext.getString(R.string.en));
        mIndexLang.put(18,mContext.getString(R.string.eo));
        mIndexLang.put(19,mContext.getString(R.string.es));
        mIndexLang.put(20,mContext.getString(R.string.et));
        mIndexLang.put(21,mContext.getString(R.string.eu));
        mIndexLang.put(22,mContext.getString(R.string.fa));
        mIndexLang.put(23,mContext.getString(R.string.fi));
        mIndexLang.put(24,mContext.getString(R.string.fr));
        mIndexLang.put(25,mContext.getString(R.string.gd));
        mIndexLang.put(26,mContext.getString(R.string.gl));
        mIndexLang.put(27,mContext.getString(R.string.gu));
        mIndexLang.put(28,mContext.getString(R.string.he));
        mIndexLang.put(29,mContext.getString(R.string.hi));
        mIndexLang.put(30,mContext.getString(R.string.hr));
        mIndexLang.put(31,mContext.getString(R.string.ht));
        mIndexLang.put(32,mContext.getString(R.string.hu));
        mIndexLang.put(33,mContext.getString(R.string.hy));
        mIndexLang.put(34,mContext.getString(R.string.id));
        mIndexLang.put(35,mContext.getString(R.string.is));
        mIndexLang.put(36,mContext.getString(R.string.it));
        mIndexLang.put(37,mContext.getString(R.string.ja));
        mIndexLang.put(38,mContext.getString(R.string.jv));
        mIndexLang.put(39,mContext.getString(R.string.ka));
        mIndexLang.put(40,mContext.getString(R.string.kk));
        mIndexLang.put(41,mContext.getString(R.string.km));
        mIndexLang.put(42,mContext.getString(R.string.kn));
        mIndexLang.put(43,mContext.getString(R.string.ko));
        mIndexLang.put(44,mContext.getString(R.string.ky));
        mIndexLang.put(45,mContext.getString(R.string.la));
        mIndexLang.put(46,mContext.getString(R.string.lb));
        mIndexLang.put(47,mContext.getString(R.string.lo));
        mIndexLang.put(48,mContext.getString(R.string.lt));
        mIndexLang.put(49,mContext.getString(R.string.lv));
        mIndexLang.put(50,mContext.getString(R.string.mg));
        mIndexLang.put(51,mContext.getString(R.string.mhr));
        mIndexLang.put(52,mContext.getString(R.string.mi));
        mIndexLang.put(53,mContext.getString(R.string.mk));
        mIndexLang.put(54,mContext.getString(R.string.ml));
        mIndexLang.put(55,mContext.getString(R.string.mn));
        mIndexLang.put(56,mContext.getString(R.string.mr));
        mIndexLang.put(57,mContext.getString(R.string.mrj));
        mIndexLang.put(58,mContext.getString(R.string.ms));
        mIndexLang.put(59,mContext.getString(R.string.mt));
        mIndexLang.put(60,mContext.getString(R.string.my));
        mIndexLang.put(61,mContext.getString(R.string.ne));
        mIndexLang.put(62,mContext.getString(R.string.nl));
        mIndexLang.put(63,mContext.getString(R.string.no));
        mIndexLang.put(64,mContext.getString(R.string.pa));
        mIndexLang.put(65,mContext.getString(R.string.pap));
        mIndexLang.put(66,mContext.getString(R.string.pl));
        mIndexLang.put(67,mContext.getString(R.string.pt));
        mIndexLang.put(68,mContext.getString(R.string.ro));
        mIndexLang.put(69,mContext.getString(R.string.ru));
        mIndexLang.put(70,mContext.getString(R.string.si));
        mIndexLang.put(71,mContext.getString(R.string.sk));
        mIndexLang.put(72,mContext.getString(R.string.sl));
        mIndexLang.put(73,mContext.getString(R.string.sq));
        mIndexLang.put(74,mContext.getString(R.string.sr));
        mIndexLang.put(75,mContext.getString(R.string.su));
        mIndexLang.put(76,mContext.getString(R.string.sv));
        mIndexLang.put(77,mContext.getString(R.string.sw));
        mIndexLang.put(78,mContext.getString(R.string.ta));
        mIndexLang.put(79,mContext.getString(R.string.te));
        mIndexLang.put(80,mContext.getString(R.string.tg));
        mIndexLang.put(81,mContext.getString(R.string.th));
        mIndexLang.put(82,mContext.getString(R.string.tl));
        mIndexLang.put(83,mContext.getString(R.string.tr));
        mIndexLang.put(84,mContext.getString(R.string.tt));
        mIndexLang.put(85,mContext.getString(R.string.udm));
        mIndexLang.put(86,mContext.getString(R.string.uk));
        mIndexLang.put(87,mContext.getString(R.string.ur));
        mIndexLang.put(88,mContext.getString(R.string.uz));
        mIndexLang.put(89,mContext.getString(R.string.vi));
        mIndexLang.put(90,mContext.getString(R.string.xh));
        mIndexLang.put(91,mContext.getString(R.string.yi));
        mIndexLang.put(92,mContext.getString(R.string.zh));

    }
}
