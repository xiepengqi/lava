package lava.core.keyword;

import lava.core.Form;

/**
 * Created by xie,pengqi on 2018/4/16.
 */
public class ExportForm extends Form{
    @Override
    public void parse() {
        super.parse();

    }

    @Override
    public void check() {
        super.check();

    }

    @Override
    public void run() throws Exception {
        super.run();

        for(String arg:this.args){
            this.inCode.getExports().put(arg,this.parseFormArg(arg));
        }
    }
}
