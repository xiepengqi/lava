package lava.core.keyword;

import lava.core.Form;

/**
 * Created by xie,pengqi on 2017/9/14.
 */
public class CatchForm extends Form {

    @Override
    public void parse() {
        super.parse();
        Form form;

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


        Form form;
        for (String formId : this.args.subList(0, this.args.size()-1)) {
            form = this.inCode.getFormMap().get(formId);
            if (form != null) {
                try{
                    runFormSeq(form.getFormSeqWhichRunBy(this), null);
                } catch (Exception e){
                    this.value=e;
                    this.type=e.getClass();
                }
            }
        }
    }
}
