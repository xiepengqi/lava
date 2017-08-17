package lava.core.keyword;

import lava.core.Form;
import lava.core.Sub;

public class QDefForm extends Form {
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
		processIsDef();
	}

	private void processIsDef() {
		boolean isDef = false;
		for (Sub sub : this.inSubSeq) {
			if (sub.getDataMap().getMap().containsKey(this.args.get(0))) {
				isDef = true;
			}
		}
		if (this.inCode.getDataMap().getMap().containsKey(this.args.get(0))) {
			isDef = true;
		}
		if (isDef) {
			this.value = true;
		} else {
			this.value = false;
		}
		this.type = Boolean.class;
	}

}
