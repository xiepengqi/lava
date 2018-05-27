package lava.core.keyword;

import lava.core.Data;
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

        if(this.args.size() == 0){
        	this.inCode.getExports().getMap().putAll(this.inCode.getDataMap().getMap());
        	return;
        }
        
        for(String arg:this.args){
        	Data data=this.parseFormArg(arg);
        	
            this.inCode.getExports().putData(arg, data);
        }
    }
}
