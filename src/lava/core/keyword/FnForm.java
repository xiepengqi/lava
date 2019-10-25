package lava.core.keyword;

import lava.constant.Constants;
import lava.core.Data;
import lava.core.Form;
import lava.core.Sub;
import lava.core.SysError;
import lava.util.JavaUtil;
import lava.util.StringUtil;

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

		if(this.fnName.contains(Constants.javaChar)){
			Data data;
			if (Constants.javaChar.equals(this.fnName)) {
				data = JavaUtil.processField(newParseArgs);
			} else {
				String javaName = this.fnName;
				if (this.fnName.contains(Constants.expand)) {
					String[] strs = this.fnName.split("\\" + Constants.javaChar);
					for (String str : strs) {
						if (str.startsWith(Constants.expand)) {
							Data d = parseFormArg(str.substring(1));
							javaName = javaName.replace(str, StringUtil.toString(d.getValue()));
						}
					}
				}
				data = JavaUtil.processMethod(javaName, newParseArgs);
			}
			this.value = data.getValue();
			this.type = data.getType();
		} else {
			Sub sub = getSubFromScope(this.fnName);
			if(sub==null){
				throw new SysError(this, this.fnName);
			}
			runSub(sub,newParseArgs,null, argMap);
			this.type=(sub).getAsForm().getType();
			this.value=(sub).getAsForm().getValue();
		}
	}

}
