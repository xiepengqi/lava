package lava.core.keyword;

import lava.Main;
import lava.core.Data;
import lava.core.Form;

import java.util.List;

/**
 * Created by xie,pengqi on 2017/12/12.
 */
public class LinkForm extends Form {

    @Override
    public void parse() {
        super.check();
    }

    @Override
    public void check() {
        super.check();
    }

    @Override
    public void run() throws Exception {
        super.run();
        List<Data> parseArgs = this.parseFormArgs(this.args);

        Object sub=parseArgs.get(0).getValue();

        if(parseArgs.size()>1){
            Main.subLinks.put(sub,parseArgs.get(1).getValue());
        }else{
            Main.subLinks.remove(sub);
        }
    }
}