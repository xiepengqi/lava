package lava.core.keyword;

import lava.core.Data;
import lava.core.Form;

import java.util.List;
import java.util.Objects;

public class QEqForm extends Form {

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
		if (isEqual(parseArgs)) {
			this.value = true;
			this.type = this.value.getClass();
		} else {
			this.value = false;
			this.type = this.value.getClass();
		}
	}

	private boolean isEqual(List<Data> parseArgs) {
		Data obj = parseArgs.get(0);
		for (Data o : parseArgs.subList(1, parseArgs.size())) {
			if (Objects.equals(obj.getValue(), o.getValue())) {
				obj = o;
			} else {
				return false;
			}
		}
		return true;
	}
}
