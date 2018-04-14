package lava.core.keyword;

import java.util.List;

import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.SysError;
import lava.core.Sub;
import lava.util.Util;

public class WhileForm extends Form {
	@Override
	public void parse() {

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
			throw new SysError(Util.getErrorStr(this, this.args.get(0)));
		}

		if (parseArgs.get(1).getValue() instanceof Sub) {
			bodySub = (Sub) parseArgs.get(1).getValue();

			runSub(flagSub,null,null,this);

			while ((Boolean) flagSub.getAsForm().getValue()) {
				runSub(bodySub,null,null,this);

				if (bodySub.getAsForm().getValue() instanceof Boolean) {
					if (!(Boolean) bodySub.getAsForm().getValue()) {
						break;
					}
				}

				runSub(flagSub,null,null,this);
			}

			this.value = null;
			this.type = void.class;
		} else {
			throw new SysError(Util.getErrorStr(this, this.args.get(1)));
		}
	}
}
