package lava.core.keyword;

import java.util.List;

import lava.constant.Constants;
import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.util.JavaUtil;

public class JavaForm extends Form {

	@Override
	public void parse() throws Exception {

	}

	@Override
	public void check() {
		super.check();
	}

	@Override
	public void run() throws Exception {
		super.run();
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);
		if (Constants.javaChar.equals(this.fnName)) {
			this.value = JavaUtil.processField(this, parseArgs);
			return;
		}
		this.value = JavaUtil.processMethod(this, parseArgs);
	}
}
