package lava.core.keyword;

import java.util.List;

import lava.core.DataMap.Data;
import lava.core.Form;

public class TypeForm extends Form {

	@Override
	public void parse() {
		super.parse();

	}

	@Override
	public void check() {
		super.check();
	}

	@Override
	public void run() throws Exception {
		super.run();
		List<Data> parseArgs = this.parseFormArgs(this.args);
		this.value = parseArgs.get(0).getType();
		this.type = parseArgs.get(0).getType().getClass();
	}
}
