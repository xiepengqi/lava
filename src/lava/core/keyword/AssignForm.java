package lava.core.keyword;

import java.util.List;

import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.util.Util;

public class AssignForm extends Form {
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
		processFormAssign();
	}

	private void processFormAssign() throws Exception {
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);
		DataInfo value = parseArgs.get(parseArgs.size() - 1);

		for (DataInfo data : parseArgs.subList(0, parseArgs.size() - 1)) {
			if (null == data.getFundIn()) {
				Util.runtimeError(this, data.getSource());
			}

			data.setValue(value.getValue());
			data.setType(value.getType());
		}

		this.value = value.getValue();
		this.type = value.getType();
	}
}
