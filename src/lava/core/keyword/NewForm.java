package lava.core.keyword;

import java.util.List;

import lava.core.Data;
import lava.core.Form;
import lava.util.JavaUtil;

public class NewForm extends Form {
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
		Data data = JavaUtil.processNew(parseArgs);
		this.value = data.getValue();
		this.type = data.getType();
	}
}
