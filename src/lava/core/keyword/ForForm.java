package lava.core.keyword;

import java.util.ArrayList;
import java.util.List;

import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.Sub;
import lava.util.Util;

public class ForForm extends Form {
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
		processFormFor();
	}

	private void processFormFor() throws Exception {
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);

		if (parseArgs.get(0).getValue() instanceof Sub) {
			Sub sub = (Sub) parseArgs.get(0).getValue();

			long init = 0;
			long step = 1;
			long limit = 0;

			if (parseArgs.size() == 2) {
				limit = Long.parseLong(parseArgs.get(1).getValue().toString());
			}

			if (parseArgs.size() == 3) {
				init = Long.parseLong(parseArgs.get(1).getValue().toString());
				limit = Long.parseLong(parseArgs.get(2).getValue().toString());

				step = init <= limit ? 1 : -1;
			}

			if (parseArgs.size() == 4) {
				init = Long.parseLong(parseArgs.get(1).getValue().toString());
				step = Long.parseLong(parseArgs.get(2).getValue().toString());
				limit = Long.parseLong(parseArgs.get(3).getValue().toString());
			}

			for (long i = init; init <= limit ? i <= limit : i >= limit; i = i + step) {
				List<Object> values = new ArrayList<Object>();
				values.add(String.valueOf(i));

				runSub(sub,null,values,this);

				if (sub.getAsForm().getValue() instanceof Boolean) {
					if (!(Boolean) sub.getAsForm().getValue()) {
						break;
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
