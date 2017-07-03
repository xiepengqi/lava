package lava.core.keyword;

import java.util.ArrayList;
import java.util.List;

import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.Sub;
import lava.util.Util;

public class ForeachForm extends Form {
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
		processFormForeach();
	}

	@SuppressWarnings("rawtypes")
	private void processFormForeach() throws Exception {
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);

		if (parseArgs.get(parseArgs.size() - 1).getValue() instanceof Sub) {
			Sub sub = (Sub) parseArgs.get(parseArgs.size() - 1).getValue();

			for (int i = 0; i < parseArgs.size() - 1; i++) {
				for (Object j : (Iterable) parseArgs.get(i).getValue()) {
					List<Object> values = new ArrayList<Object>();
					values.add(j);
					sub.getDataMap().put("$args", values);
					sub.getDataMap().put("$0", j);
					sub.getDataMap().put("$-1", j);

					sub.run();

					if (sub.getAsForm().getValue() instanceof Boolean) {
						if (!(Boolean) sub.getAsForm().getValue()) {
							break;
						}
					}
				}
			}

			this.value = null;
			this.type = void.class;
		} else {
			Util.runtimeError(this, this.args.get(0));
		}
	}
}
