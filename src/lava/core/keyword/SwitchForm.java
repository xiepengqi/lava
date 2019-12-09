package lava.core.keyword;

import lava.core.Form;
import lava.core.Data;

public class SwitchForm extends Form{
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
		
		int i=-1;
		Data main = null;
		Data key = null;
		Data value = null;
		for(String arg:this.args){
			i++;
			if(i == 0){
				main=runAndParseFormArg(arg);
				continue;
			}
			if(i%2 ==  1){
				key=runAndParseFormArg(arg);
				continue;
			}
			if(i%2 == 0){
				if((main.getValue() == null && key.getValue() == null)){
					value=runAndParseFormArg(arg);
					break;
				}
				if(main.getValue() !=null){
					if(main.getValue().equals(key.getValue())){
						value=runAndParseFormArg(arg);
						break;
					}
				}else if(key.getValue().equals(main.getValue())){
					value=runAndParseFormArg(arg);
					break;
				}
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
