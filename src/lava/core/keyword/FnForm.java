package lava.core.keyword;

import lava.constant.Constants;
import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.Sub;
import lava.util.Util;

import java.util.ArrayList;
import java.util.List;

public class FnForm extends Form {
	@Override
	public void parse() throws Exception {
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
			Util.runtimeError(this,this.fnName);
		}

		List<DataInfo> newParseArgs=new ArrayList<DataInfo>();
		for(String arg:this.args){
			if(!Constants.in_args.equals(arg)){
				newParseArgs.add(this.parseFormArg(arg));
			}
			DataInfo $args=this.parseFormArg(arg);

			if($args.getValue()==null){
				newParseArgs.add($args);
			}

			if($args.getValue() instanceof Object[]){
				for(Object obj:(Object[])$args.getValue()){
					newParseArgs.add(new DataInfo(Object.class,obj,null,Constants.in_args));
				}
			}else if($args.getValue() instanceof List){
				for(Object obj:(List)$args.getValue()){
					newParseArgs.add(new DataInfo(Object.class,obj,null,Constants.in_args));
				}
			}else{
				newParseArgs.add($args);
			}
		}

		runSub(sub,newParseArgs,null,this);
		this.type=(sub).getAsForm().getType();
		this.value=(sub).getAsForm().getValue();
	}

}
