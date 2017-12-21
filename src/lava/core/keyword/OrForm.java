package lava.core.keyword;

import lava.core.DataMap.DataInfo;
import lava.core.Form;

public class OrForm extends Form {
	@Override
	public void parse() {
		super.parse();
		Form form = null;
		for (String arg : this.args) {
			form = this.inCode.getFormMap().get(arg);
			if (null != form) {
				form.markRunBy(this);
			}
		}
	}

	@Override
	public void check() {
		super.check();

	}

	@Override
	public void run() throws Exception {
		super.run();

		Form form = null;
		boolean flag = false;
		for (String arg : this.args) {
			form = this.inCode.getFormMap().get(arg);
			if (null != form) {
				runFormSeq(form.getFormSeqWhichRunBy(this));
				flag = (Boolean) form.getValue();
			} else {
				DataInfo data = parseFormArg(arg);
				flag = (Boolean) data.getValue();
			}
			if (flag) {
				break;
			}
		}

		this.value = flag;
		this.type = Boolean.class;
	}

}
