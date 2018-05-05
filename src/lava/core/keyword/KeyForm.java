package lava.core.keyword;


import java.util.List;
import java.util.Map;

import lava.constant.Constants;
import lava.core.DataMap.Data;
import lava.core.Form;
import lava.core.Sub;
import lava.core.SysError;
import lava.util.StringUtil;
import lava.util.Util;

public class KeyForm extends Form{
	private String fieldName=Constants.empty;
	
	@Override
	public void parse() {
		if(Constants.sepOrObjChar.equals(this.fnName)){
			return;
		}
		this.fieldName=this.fnName.substring(1);
	}

	@Override
	public void check() {
		super.check();
	}

	@Override
	public void run() throws Exception {
		super.run();
		
		Object result;
		
		List<Data> parseArgs=this.parseFormArgs(this.args);
		if(!(parseArgs.get(0).getValue() instanceof Map)){
			throw new SysError(this, this.args.get(0));
		}
		Map map=(Map)parseArgs.get(0).getValue();
		
		if(StringUtil.isBlank(fieldName)){
			result = map.get(parseArgs.get(1).getValue());
			this.value=result;
			this.type=Data.getClass(result);
		} else {
			if(!(map.get(this.fieldName) instanceof Sub)){
				throw new SysError(this, this.fieldName);
			}
			
			Sub sub=(Sub)map.get(this.fieldName);
			this.runSub(sub, parseArgs.subList(1, parseArgs.size()), null);
			
			this.value=sub.getAsForm().getValue();
			this.type=sub.getAsForm().getType();
		}
		
		
	}
}
