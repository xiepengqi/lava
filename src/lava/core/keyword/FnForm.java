package lava.core.keyword;

import java.util.ArrayList;
import java.util.List;

import lava.Main;
import lava.constant.Constants;
import lava.core.DataMap;
import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.Sub;
import lava.util.StringUtil;
import lava.util.Util;

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
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);
		DataInfo subData = getSubFromScope(this.fnName);

		if(!runSubLink(subData,parseArgs)){
			DataInfo dataInfo=runSub(this.fnName,(Sub)subData.getValue(),parseArgs);
			this.type=dataInfo.getType();
			this.value=dataInfo.getValue();
		}
	}

	private boolean runSubLink(DataInfo subData,List<DataInfo> parseArgs) throws Exception {
		boolean in=false;
		if(subData==null){
			return in;
		}

		Object key;

		Object subLink= Main.subLinks.remove(subData.getValue());
		value=subLink;
		if(subLink==null){
			subLink= Main.subLinks.remove(((Sub)subData.getValue()).getName());
			key=((Sub)subData.getValue()).getName();
			value=subLink;
		}else{
			key=subLink;
		}

		if(subLink==null){
			return in;
		}

		if(!(subLink instanceof Sub)){
			subLink=getSubFromScope(StringUtil.toString(subLink)).getValue();
		}

		if(subLink ==null){
			Main.subLinks.put(key,value);
			return in;
		}

		in=true;
		List<DataInfo> args=new ArrayList<DataInfo>();
		args.add(subData);
		args.addAll(parseArgs);

		runSub(((Sub)subLink).getName(),(Sub)subLink,args);


		Main.subLinks.put(key,value);
		return in;
	}



	private DataInfo getSubFromScope(String fnName) {
		if (fnName.equals(this.fnName)&&StringUtil.isFormId(this, this.fnName)) {
			Form form=this.inCode.getFormMap().get(this.fnName);
			Object obj = form.getValue();
			if (obj instanceof Sub) {
				return new DataInfo(Sub.class, obj, form.getSource(), obj);
			}
		}

		DataInfo findSub;
		for (Sub sub : this.inSubSeq) {
			findSub = findSub(sub.getDataMap(), fnName);
			if (null != findSub) {
				return findSub;
			}
			findSub = findSub(sub.getClosure(), fnName);
			if (null != findSub) {
				return findSub;
			}
		}

		findSub = findSub(this.inCode.getDataMap(), fnName);
		if (null != findSub) {
			return findSub;
		}
		return null;
	}

	public DataInfo findSub(DataMap dataMap, String fnName) {
		DataInfo data = dataMap.get(Constants.subPrefix + fnName);
		if (null == data) {
			data = dataMap.get(fnName);
		}

		if (data != null && data.getValue() instanceof Sub) {
			return data;
		}
		return null;
	}
}
