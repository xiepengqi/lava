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
			runSub((Sub)subData.getValue(),parseArgs);
		}
	}

	private boolean runSubLink(DataInfo subData,List<DataInfo> parseArgs) throws Exception {
		boolean in=false;
		if(subData==null){
			return in;
		}

		List<String> subLink= Main.subLinks.remove(((Sub)subData.getValue()).getName());
		if(subLink==null||subLink.size()==0){
			return in;
		}
		in=true;
		DataInfo subDataInLink;
		for(String subName:subLink){
			subDataInLink = getSubFromScope(subName);
			List<DataInfo> args=new ArrayList<DataInfo>();
			args.add(subData);
			args.addAll(parseArgs);

			runSub((Sub)subDataInLink.getValue(),args);
		}
		Main.subLinks.put(((Sub)subData.getValue()).getName(),subLink);
		return in;
	}

	private void runSub(Sub sub,List<DataInfo> parseArgs) throws Exception {
		if (null != sub) {
			for (int i = 0; i < parseArgs.size(); i++) {
				sub.getDataMap().put("$" + i, parseArgs.get(i));
				sub.getDataMap().put("$-" + (parseArgs.size() - i), parseArgs.get(i));
			}

			List<Object> values = new ArrayList<Object>();
			Util.splitArgs(parseArgs, values, null);
			sub.getDataMap().put("$args", values);
			sub.run();
			this.value = sub.getAsForm().getValue();
			this.type = sub.getAsForm().getType();
			return;
		} else {
			Util.runtimeError(this, sub.getName());
		}
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
