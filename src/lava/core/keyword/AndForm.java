package lava.core.keyword;

import lava.core.Data;
import lava.core.Form;
import lava.util.Util;

public class AndForm extends Form {
	@Override
	public void parse() {
		super.parse();

		Form form;
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

		Form form;
		boolean flag = false;
		for (String arg : this.args) {
			form = this.inCode.getFormMap().get(arg);
			if (null != form) {
				runFormSeq(form.getFormSeqWhichRunBy(this),null);
				flag = Util.isValid(form.getValue());
			} else {
				Data data = parseFormArg(arg);
				flag = Util.isValid(data.getValue()) ;
			}
			if (!flag) {
				break;
			}
		}

		this.value = flag;
		this.type = Boolean.class;

	}

}
