package lava.core.keyword;

import java.util.List;

import lava.constant.Constants;
import lava.core.Data;
import lava.core.Form;
import lava.util.JavaUtil;

public class JavaForm extends Form {

	@Override
	public void parse() {

	}

	@Override
	public void check() {
		super.check();
	}

	@Override
	public void run() throws Exception {
		super.run();
		List<Data> parseArgs = this.parseFormArgs(this.args);
		String fnName = (String)this.parseFormArg(this.fnName).getValue();
		if (Constants.javaChar.equals(fnName)) {
			this.value = JavaUtil.processField(parseArgs);
			this.type = Data.getClass(this.value);
			return;
		}

		this.value = JavaUtil.processMethod(fnName, parseArgs);
		this.type = Data.getClass(this.value);
	}
}
