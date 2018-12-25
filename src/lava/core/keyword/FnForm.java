package lava.core.keyword;

import lava.constant.Constants;
import lava.core.Data;
import lava.core.Form;
import lava.core.Sub;
import lava.util.JavaUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		List<Data> newParseArgs=new ArrayList<Data>();
		Map argMap = new HashMap();
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
			}else if(args.getValue() instanceof Map){
				argMap.putAll((Map) args.getValue());
			}else{
				newParseArgs.add(args);
			}
		}

		if(sub==null){
			String fnName = (String)this.parseFormArg(this.fnName).getValue();
			if (Constants.javaChar.equals(fnName)) {
				this.value = JavaUtil.processField(newParseArgs);
				this.type = Data.getClass(this.value);
				return;
			}

			this.value = JavaUtil.processMethod(fnName, newParseArgs);
			this.type = Data.getClass(this.value);
		} else {
			runSub(sub,newParseArgs,null, argMap);
			this.type=(sub).getAsForm().getType();
			this.value=(sub).getAsForm().getValue();
		}
	}

}
