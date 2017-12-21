package lava.core.keyword;

import java.util.List;

import lava.core.DataMap;
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
		DataInfo value = this.parseFormArg(this.args.get(this.args.size() - 1));

		for (String arg : this.args.subList(0, this.args.size() - 1)) {
			DataInfo data=this.parseFormArg(arg);
			if (!(data.getIn() instanceof DataMap)) {
				Util.runtimeError(this, arg);
			}

			data.setValue(value.getValue());
			data.setType(value.getType());
		}

		this.value = value.getValue();
		this.type = value.getType();
	}
}
