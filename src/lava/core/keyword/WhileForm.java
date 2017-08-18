package lava.core.keyword;

import java.util.List;

import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.Sub;
import lava.util.Util;

public class WhileForm extends Form {
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
		processFormWhile();
	}

	private void processFormWhile() throws Exception {
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);
		Sub flagSub = null;
		Sub bodySub = null;

		if (parseArgs.get(0).getValue() instanceof Sub) {
			flagSub = (Sub) parseArgs.get(0).getValue();
		} else {
			Util.runtimeError(this, this.args.get(0));
		}

		if (parseArgs.get(1).getValue() instanceof Sub) {
			bodySub = (Sub) parseArgs.get(1).getValue();
			flagSub.run();

			while ((Boolean) flagSub.getAsForm().getValue()) {
				bodySub.run();

				if (bodySub.getAsForm().getValue() instanceof Boolean) {
					if (!(Boolean) bodySub.getAsForm().getValue()) {
						break;
					}
				}

				flagSub.run();
			}

			this.value = null;
			this.type = void.class;
		} else {
			Util.runtimeError(this, this.args.get(1));
		}
	}
}