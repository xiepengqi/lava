package lava.core.keyword;

import java.util.List;

import lava.core.DataMap.DataInfo;
import lava.core.Form;

public class QEqForm extends Form {

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
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);
		if (isEqual(parseArgs)) {
			this.value = true;
			this.type = this.value.getClass();
		} else {
			this.value = false;
			this.type = this.value.getClass();
		}
	}

	private boolean isEqual(List<DataInfo> parseArgs) {
		DataInfo obj = parseArgs.get(0);
		for (DataInfo o : parseArgs.subList(1, parseArgs.size())) {
			if (obj.getValue() == o.getValue()) {
				obj = o;
			} else {
				return false;
			}
		}
		return true;
	}
}
