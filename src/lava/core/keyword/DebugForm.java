package lava.core.keyword;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lava.Main;
import lava.core.DataMap.DataInfo;
import lava.core.Form;

public class DebugForm extends Form {
	@Override
	public void parse() throws Exception {
		super.parse();

		Form form;
		for (String arg : this.args.subList(1, this.args.size())) {
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

	@SuppressWarnings("rawtypes")
	@Override
	public void run() throws Exception {
		super.run();

		DataInfo data = parseFormArg(this.args.get(0));
		Map<Object, Boolean> rollback = new HashMap<Object, Boolean>();

		if (data.getValue() instanceof Map) {
			Map map = (Map) data.getValue();
			for (Object codeId : map.keySet()) {
				Main.codes.get(codeId).setDebug((Boolean) map.get(codeId));
				rollback.put(codeId, !(Boolean) map.get(codeId));
			}
		} else if (data.getValue() instanceof Collection) {
			for (Object codeId : (Collection) data.getValue()) {
				Main.codes.get(codeId).setDebug(true);
				rollback.put(codeId, false);
			}
		}

		if (this.args.size() == 1) {
			this.value = null;
			this.type = void.class;
			return;
		}

		Action action = new Action() {
			@Override
			public void beforeRun(Form form) {
				form.setDebug(true);
			}

			@Override
			public void afterRun(Form form) {
				form.setDebug(false);
			}
		};

		Form form = null;
		for (String formId : this.args.subList(1, this.args.size())) {
			form = this.inCode.getFormMap().get(formId);
			if (form != null) {
				runFormSeq(form.getFormSeqWhichRunBy(this), action);
			}
		}

		for (Object codeId : rollback.keySet()) {
			Main.codes.get(codeId).setDebug(rollback.get(codeId));
		}
	}
}
