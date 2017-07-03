package lava.core.keyword;

import java.util.List;

import lava.constant.Constants;
import lava.core.DataMap.DataInfo;
import lava.core.Form;

public class AsForm extends Form {

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
		processFormAs();
	}

	@SuppressWarnings("rawtypes")
	private void processFormAs() throws Exception {
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);

		Class classObj = null;
		if (this.args.size() == 1) {
			this.value = parseArgs.get(0).getValue();
			this.type = null == parseArgs.get(0).getValue() ? void.class : parseArgs.get(0).getValue().getClass();
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
	private Class getClassByName(String className) {
		Class classObj = null;
		if (null != Constants.baseTypes.get(className)) {
			classObj = Constants.baseTypes.get(className);
		} else {
			try {
				classObj = Class.forName(className);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return classObj;
	}
}
