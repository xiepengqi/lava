package lava.core.keyword;

import java.util.List;

import lava.core.DataMap.Data;
import lava.core.Form;

public class ReturnForm extends Form {
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
		this.inSubSeq.get(0).setIsReturn(true);

		if(parseArgs.size()>0) {
			Data data = parseArgs.get(parseArgs.size() - 1);
			this.value = data.getValue();
			this.type = data.getType();
		}
	}
}
