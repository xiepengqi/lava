package lava.core.keyword;

import lava.core.Form;
import lava.core.DataMap.DataInfo;

public class AskForm extends Form{
	@Override
	public void parse() {
		super.parse();

		
		Form form;;
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
		DataInfo main = null;
		DataInfo key = null;
		DataInfo value = null;
		for(String arg:this.args){
			i++;
			if(i == 0){
				main=getParseArg(arg);
				continue;
			}
			if(i%2 ==  1){
				key=getParseArg(arg);
				continue;
			}
			if(i%2 == 0){
				if((main.getValue() == null && key.getValue() == null)){
					value=getParseArg(arg);
					break;
				}
				if(main.getValue() !=null){
					if(main.getValue().equals(key.getValue())){
						value=getParseArg(arg);
						break;
					}
				}else if(key.getValue().equals(main.getValue())){
					value=getParseArg(arg);
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
	
	private DataInfo getParseArg(String arg) throws Exception{
		Form form=this.inCode.getFormMap().get(arg);
		DataInfo data;
		if(form!=null){
			runFormSeq(form.getFormSeqWhichRunBy(this),null);
			data=new DataInfo(form.getType(),form.getValue());
		}else{
			data=this.parseFormArg(arg);
		}
		return data;
	}
}
