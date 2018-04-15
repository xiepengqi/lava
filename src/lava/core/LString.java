package lava.core;

import java.util.ArrayList;
import java.util.List;

import lava.constant.RegexConstants;
import lava.core.DataMap.DataInfo;
import lava.util.StringUtil;

public class LString {
	private List<String> vars=new ArrayList<String>();
	private String source;
	
	public LString(Object val) {
		this.source=String.valueOf(val);
		initVars();
	}

	private void initVars() {
		String stringSource=this.source;
		this.vars = StringUtil.getMatchers(RegexConstants.extractLStringVar, stringSource);
		
	}

	public String toString(){
		return this.source;
	}

	public Object parse(Form form) {
		StringBuilder result=new StringBuilder();
		String originSource=this.source;
		for(String var:vars){
			String[] strs=originSource.split(StringUtil.escapeReg(var),2);
			
			String originVar=var.substring(1,var.length()-1);
			DataInfo data=form.parseFormArg(originVar);
			result.append(strs[0]);
			result.append(String.valueOf(data.getValue()));
			
			originSource=strs[1];
		}
		result.append(originSource);
		
		return result.toString();
	}
}
