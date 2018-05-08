package lava.core.keyword;

import java.util.Arrays;

import lava.constant.Constants;
import lava.core.DataMap;
import lava.core.Form;
import lava.core.Sub;
import lava.util.StringUtil;
import lava.util.Util;

public class SubForm extends Form {

	@Override
	public void parse() {
		super.parse();

		Sub sub = new Sub();

		String[] nameArgs=this.fnName.split(Constants.sepOrObjChar,2);
		this.fnName= nameArgs[0];

		if(nameArgs.length > 1){
			sub.getArgs().addAll(Arrays.asList(nameArgs[1].split(Constants.sepOrObjChar)));
		}

		sub.setName(this.fnName.replaceFirst(Constants.subPrefix, Constants.empty));
		sub.setInCode(this.inCode);
		sub.setAsForm(this);
		this.asSub = sub;

		markScopeForForm(this, sub);

	}

	private void markScopeForForm(Form form, Sub func) {
		for (String arg : form.getElems()) {
			if (null != this.inCode.getFormMap().get(arg)) {
				this.inCode.getFormMap().get(arg).getInSubSeq().add(func);
				markScopeForForm(this.inCode.getFormMap().get(arg), func);
			}
		}
	}

	@Override
	public void check() {
		super.check();

		for(String argName:this.asSub.getArgs()){
			if(argName.startsWith(Constants.expand)){
				argName=argName.substring(1);
			}
			if(StringUtil.isBlank(argName)){
				continue;
			}
			if(!StringUtil.isDefVarAble(argName)){
				Util.syntaxError(this, argName);
			}
		}
		
		if (StringUtil.isEmpty(this.asSub.getName())) {
			return;
		}

		DataMap dataMap = null;
		if (this.inSubSeq.size() > 0) {
			dataMap = this.inSubSeq.get(0).getDataMap();
			
		} else {
			dataMap = this.inCode.getDataMap();
		}
		
		if(dataMap.getMap().containsKey(this.fnName)){
			Util.syntaxError(this, this.fnName);
		}else{
			dataMap.put(this.fnName, this.asSub);
		}
	}

	@Override
	public void run() throws Exception {
		super.run();

		this.type = Sub.class;
		this.value = this.asSub;
	}

}
