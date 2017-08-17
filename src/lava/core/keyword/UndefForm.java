package lava.core.keyword;

import java.util.List;

import lava.core.Code;
import lava.core.DataMap;
import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.Sub;
import lava.util.Util;

public class UndefForm extends Form {
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

		List<DataInfo> prepareArgs = this.parseFormArgs(this.args);
		int i = 0;
		for (DataInfo data : prepareArgs) {
			DataMap dataMap = null;
			Object fundIn = data.getFundIn();
			if (fundIn instanceof Code) {
				dataMap = ((Code) fundIn).getDataMap();
			} else if (fundIn instanceof Sub) {
				dataMap = ((Sub) fundIn).getDataMap();
			} else {
				Util.runtimeError(this, data.getSource());
			}
			dataMap.getMap().remove(this.args.get(i));

			this.value = data.getValue();
			this.type = data.getType();

			i++;
		}

	}
}
