package lava.core.keyword;

import lava.core.Data;
import lava.core.Form;
import lava.util.Util;

public class IfForm extends Form {

	@Override
	public void parse() {
		super.parse();

		Form form;
		for(String arg:this.args){
			form = this.inCode.getFormMap().get(arg);
			if(form !=null){
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

		int i=0;
		Data key = null;
		Data value = null;
		for(String arg:this.args){
			i++;

			if(i%2 ==  1){
				key=runAndParseFormArg(arg);
				continue;
			}
			if(i%2 == 0 && Util.isValid(key.getValue())){
				value = runAndParseFormArg(arg);
				break;
			}
		}
		if(value !=null){
			this.type=value.getType();
			this.value=value.getValue();
		}else if(i%2 == 1){
			this.type=key.getType();
			this.value=key.getValue();
		}
	}

}
