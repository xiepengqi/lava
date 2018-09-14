package lava.core.keyword;

import java.lang.reflect.Array;
import java.util.List;

import lava.constant.Constants;
import lava.constant.MsgConstants;
import lava.core.Data;
import lava.core.Form;
import lava.util.StringUtil;
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
		List<Data> parseArgs = this.parseFormArgs(this.args);

		Class classObj;
		if (this.args.size() == 1) {
			this.value = parseArgs.get(0).getValue();
			this.type = Data.getClass(parseArgs.get(0).getValue());
			return;
		}

		if (parseArgs.get(0).getValue() instanceof Class) {
			classObj = (Class) parseArgs.get(0).getValue();
		} else {
			String flag = StringUtil.toString(parseArgs.get(0).getValue());
			if (flag.startsWith("[")) {
				Object temp = Array.newInstance(getClassObj(flag.substring(1)), 0);
				classObj = temp.getClass();
			}else {
				classObj = getClassObj(flag);
			}
		}
		
		for (Data data : parseArgs.subList(1, parseArgs.size())) {
			data.setType(classObj);
		}

		this.value = parseArgs.get(parseArgs.size() - 1).getValue();
		this.type = classObj;
	}

	private Class getClassObj(String classFullName) throws ClassNotFoundException {
		if (Constants.baseTypes.containsKey(classFullName)){
			return Constants.baseTypes.get(classFullName);
		}
		return Class.forName(classFullName);
	}
}
