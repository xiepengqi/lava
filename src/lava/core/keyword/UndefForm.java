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

		int i = 0;
		for (String arg : this.args) {
			DataInfo data=this.parseFormArg(arg);
			DataMap dataMap = null;
			Object fundIn = data.getIn();
			if (fundIn instanceof DataMap) {
				dataMap = ((DataMap) fundIn);
			}else {
				Util.runtimeError(this, arg);
			}
			dataMap.getMap().remove(this.args.get(i));

			this.value = data.getValue();
			this.type = data.getType();

			i++;
		}

	}
}
