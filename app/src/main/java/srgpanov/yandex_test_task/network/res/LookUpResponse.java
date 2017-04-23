package srgpanov.yandex_test_task.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Пан on 10.04.2017.
 */
/**
 * ответ который приходит от АПИ словаря
 */

public class LookUpResponse {
    @SerializedName("head")
    @Expose
    private Head head;
    @SerializedName("def")
    @Expose
    private List<Def> def = null;

    public Head getHead() {
        return head;
    }

    public List<Def> getDef() {
        return def;
    }

    public class Def {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("pos")
        @Expose
        private String pos;
        @SerializedName("ts")
        @Expose
        private String ts;

        public String getText() {
            return text;
        }

        public String getPos() {
            return pos;
        }

        public String getTs() {
            return ts;
        }

        public List<Tr> getTr() {
            return tr;
        }

        @SerializedName("tr")
        @Expose
        private List<Tr> tr = null;

    }
    public class Ex {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("tr")
        @Expose
        private List<Tr_> tr = null;

        public String getText() {
            return text;
        }

        public List<Tr_> getTr() {
            return tr;
        }
    }

    public class Head {


    }
    public class Mean {

        @SerializedName("text")
        @Expose
        private String text;

        public String getText() {
            return text;
        }
    }
    public class Syn {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("pos")
        @Expose
        private String pos;
        @SerializedName("gen")
        @Expose
        private String gen;

        public String getText() {
            return text;
        }

        public String getPos() {
            return pos;
        }

        public String getGen() {
            return gen;
        }
    }
    public class Tr {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("pos")
        @Expose
        private String pos;
        @SerializedName("gen")
        @Expose
        private String gen;
        @SerializedName("syn")
        @Expose
        private List<Syn> syn = null;
        @SerializedName("mean")
        @Expose
        private List<Mean> mean = null;
        @SerializedName("ex")
        @Expose
        private List<Ex> ex = null;
        @SerializedName("asp")
        @Expose
        private String asp;

        public String getText() {
            return text;
        }

        public String getPos() {
            return pos;
        }

        public String getGen() {
            return gen;
        }

        public List<Syn> getSyn() {
            return syn;
        }

        public List<Mean> getMean() {
            return mean;
        }

        public List<Ex> getEx() {
            return ex;
        }

        public String getAsp() {
            return asp;
        }
    }
    public class Tr_ {

        @SerializedName("text")
        @Expose
        private String text;

        public String getText() {
            return text;
        }
    }
}
