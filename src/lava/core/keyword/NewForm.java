package lava.core.keyword;

import java.util.List;

import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.util.JavaUtil;

public class NewForm extends Form {
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
		this.value = JavaUtil.processNew(this, parseArgs);
	}
}
