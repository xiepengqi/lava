package lava.core.keyword;

import lava.core.Catchee;
import lava.core.Data;
import lava.core.Form;

/**
 * Created by xie,pengqi on 2017/9/14.
 */
public class CatchForm extends Form {

    @Override
    public void parse() {
        super.parse();
        Form form;

        for (String arg : this.args.subList(0, this.args.size())) {
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
        for (String arg : this.args) {
            form = this.inCode.getFormMap().get(arg);
            if (form != null) {
                try{
                    runFormSeq(form.getFormSeqWhichRunBy(this), null);
                } catch (Catchee e){
                    this.value=e.getValue();
                    this.type= Data.getClass(e.getValue());
                } catch (Throwable t){
                    this.value=t;
                    this.type=t.getClass();
                }
            }
        }
    }
}
