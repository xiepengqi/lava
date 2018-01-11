package lava.core.keyword;

import java.util.List;

import lava.constant.Constants;
import lava.constant.MsgConstants;
import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.util.Util;

public class AsForm extends Form {

	@Override
	public void parse() {
		super.parse();

	}

	@Override
	public void check() {
		super.check();
		if(this.args.size()<1){
			Util.syntaxError(this, MsgConstants.wrong_args_num);
		}
	}

	@Override
	public void run() throws Exception {
		super.run();
		processFormAs();
	}

	@SuppressWarnings("rawtypes")
	private void processFormAs() throws ClassNotFoundException {
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);

		Class classObj;
		if (this.args.size() == 1) {
			this.value = parseArgs.get(0).getValue();
			this.type = DataInfo.getClass(parseArgs.get(0).getValue());
			return;
		}

		if (parseArgs.get(0).getValue() instanceof Class) {
			classObj = (Class) parseArgs.get(0).getValue();
		} else {
			classObj = (Class) getClassByName(parseArgs.get(0).getValue().toString());
		}

		for (DataInfo data : parseArgs.subList(1, parseArgs.size())) {
			data.setType(classObj);
		}

		this.value = parseArgs.get(parseArgs.size() - 1).getValue();
		this.type = classObj;
	}

	@SuppressWarnings("rawtypes")
	private Class getClassByName(String className) throws ClassNotFoundException {
		Class classObj = null;
		if (null != Constants.baseTypes.get(className)) {
			classObj = Constants.baseTypes.get(className);
		} else {
			classObj = Class.forName(className);
		}
		return classObj;
	}
}
