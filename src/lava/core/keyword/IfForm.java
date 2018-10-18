package lava.core.keyword;

import lava.core.Data;
import lava.core.Form;
import lava.util.Util;

public class IfForm extends Form {

	@Override
	public void parse() {
		super.parse();
		if (this.args.size() < 2 || this.args.size() > 3) {
			return;
		}
		Form form = this.inCode.getFormMap().get(this.args.get(1));
		if (null != form) {
			form.markRunBy(this);
		}
		if (this.args.size() == 3) {
			form = this.inCode.getFormMap().get(this.args.get(2));
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
		processFormIf();
	}

	private void processFormIf() throws Exception {
		Form form = null;
		Data data=null;
		if (Util.isValid(parseFormArg(this.args.get(0)).getValue())) {
			form = this.inCode.getFormMap().get(this.args.get(1));
			data = parseFormArg(this.args.get(1));
		} else {
			if (this.args.size() == 3) {
				form = this.inCode.getFormMap().get(this.args.get(2));
				data = parseFormArg(this.args.get(2));
			}
		}

		if (null == form) {
			this.value = data==null ? null:data.getValue();
			this.type = Data.getType(data);
			return;
		}

		runFormSeq(form.getFormSeqWhichRunBy(this),null);

		this.value = form.getValue();
		this.type = form.getType();
	}

}
