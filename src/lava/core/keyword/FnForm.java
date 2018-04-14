package lava.core.keyword;

import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.SysError;
import lava.core.Sub;
import lava.util.Util;

import java.util.ArrayList;
import java.util.List;

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
		Sub sub = getSubFromScope(this,this.fnName);

		if(sub==null){
			throw new SysError(Util.getErrorStr(this, this.fnName));
		}

		List<DataInfo> newParseArgs=new ArrayList<DataInfo>();
		for(String arg:this.args){
			DataInfo args=new DataInfo(this.parseFormArg(arg));
			if(!"args".equals(arg)){
				newParseArgs.add(args);
				continue;
			}

			if(args.getValue()==null){
				newParseArgs.add(args);
				continue;
			}

			if(args.getValue() instanceof Object[]){
				for(Object obj:(Object[])args.getValue()){
					newParseArgs.add(new DataInfo(DataInfo.getClass(obj),obj));
				}
			}else if(args.getValue() instanceof List){
				for(Object obj:(List)args.getValue()){
					newParseArgs.add(new DataInfo(DataInfo.getClass(obj),obj));
				}
			}else{
				newParseArgs.add(args);
			}
		}

		runSub(sub,newParseArgs,null,this);
		this.type=(sub).getAsForm().getType();
		this.value=(sub).getAsForm().getValue();
	}

}
