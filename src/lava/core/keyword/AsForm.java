package lava.core.keyword;

import lava.constant.Constants;
import lava.constant.MsgConstants;
import lava.core.Data;
import lava.core.Form;
import lava.util.JavaUtil;
import lava.util.StringUtil;
import lava.util.Util;

import java.lang.reflect.Array;
import java.util.List;

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
			Data data = parseArgs.get(0);

			this.value =data.getValue();
			this.type = Data.getClass(data.getValue());

			data.setType(this.type);
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
		return JavaUtil.forName(classFullName);
	}
}
