package lava.core.keyword;

import java.util.List;

import lava.core.Data;
import lava.core.Form;

public class QSameForm extends Form {

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
		if (isSame(parseArgs)) {
			this.value = true;
			this.type = this.value.getClass();
		} else {
			this.value = false;
			this.type = this.value.getClass();
		}
	}

	private boolean isSame(List<Data> parseArgs) {
		Data obj = parseArgs.get(0);
		for (Data o : parseArgs.subList(1, parseArgs.size())) {
			if (obj.getValue() == o.getValue()) {
				obj = o;
			} else {
				return false;
			}
		}
		return true;
	}
}
