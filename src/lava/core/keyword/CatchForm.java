package lava.core.keyword;

import lava.core.Form;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xie,pengqi on 2017/9/14.
 */
public class CatchForm extends Form {
    public static List errorList=new ArrayList();

    @Override
    public void parse() throws Exception {
        super.parse();
        Form form;
        if(this.args.size()<2){
            return;
        }
        for (String arg : this.args.subList(0, this.args.size()-1)) {
            form = this.inCode.getFormMap().get(arg);
            if (null != form) {
                form.markRunBy(this);
            }
        }
    }

    @Override
    public void check() {
        super.check();

    }

    @Override
    public void run() throws Exception {
        super.run();
        Action action = new Action() {
            @Override
            public void beforeRun(Form form) {

            }

            @Override
            public void afterRun(Form form) {

            }
        };

        Form form;
        for (String formId : this.args.subList(0, this.args.size()-1)) {
            form = this.inCode.getFormMap().get(formId);
            if (form != null) {
                runFormSeq(form.getFormSeqWhichRunBy(this), action);
            }
        }
    }
}
