package lava.core.keyword;

import java.util.ArrayList;
import java.util.List;

import lava.constant.Constants;
import lava.core.Data;
import lava.core.Form;
import lava.core.Sub;
import lava.core.SysError;

public class FnForm extends Form {
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
		Sub sub = getSubFromScope(this.fnName);

		if(sub==null){
			throw new SysError(this, this.fnName);
		}

		List<Data> newParseArgs=new ArrayList<Data>();
		for(String arg:this.args){
			if(!arg.startsWith(Constants.expand)){
				newParseArgs.add(new Data(this.parseFormArg(arg)));
				continue;
			}
			Data args=new Data(this.parseFormArg(arg.substring(1)));

			if(args.getValue()==null){
				newParseArgs.add(args);
				continue;
			}

			if(args.getValue() instanceof Object[]){
				for(Object obj:(Object[])args.getValue()){
					newParseArgs.add(new Data(Data.getClass(obj),obj));
				}
			}else if(args.getValue() instanceof List){
				for(Object obj:(List)args.getValue()){
					newParseArgs.add(new Data(Data.getClass(obj),obj));
				}
			}else{
				newParseArgs.add(args);
			}
		}

		runSub(sub,newParseArgs,null);
		this.type=(sub).getAsForm().getType();
		this.value=(sub).getAsForm().getValue();
	}

}
