package lava.core.keyword;

import lava.Main;
import lava.constant.MsgConstants;
import lava.core.DataMap.Data;
import lava.core.Form;
import lava.core.Sub;
import lava.util.StringUtil;
import lava.util.Util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DebugForm extends Form {
	@Override
	public void parse() {
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
		if(this.args.size()<1){
			Util.syntaxError(this, MsgConstants.wrong_args_num);
		}else if((!StringUtil.isInnerId(this,this.args.get(0)))&&!StringUtil.isVarAble(this.args.get(0))){
			Util.syntaxError(this,MsgConstants.wrong_arg_name);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void run() throws Exception {
		super.run();

		Data data = parseFormArg(this.args.get(0));
		Map<Object, Boolean> rollback = new HashMap<Object, Boolean>();
		boolean mainDebug=Main.debug;
		if (data.getValue() instanceof Map) {
			Map map = (Map) data.getValue();
			for (Object elem : map.keySet()) {
				boolean set=(Boolean) map.get(elem);
				if(elem instanceof Sub){
					((Sub)elem).setIsDebug(set);
				}else{
					Main.codes.get(elem).setDebug(set);
				}
				rollback.put(elem, !set);
			}
		} else if (data.getValue() instanceof Collection) {
			for (Object elem : (Collection) data.getValue()) {
				if(elem instanceof Sub){
					((Sub)elem).setIsDebug(true);
				}else{
					Main.codes.get(elem).setDebug(true);
				}
				rollback.put(elem, false);
			}
		} else if (data.getValue() instanceof Boolean){
			Main.debug=(Boolean)data.getValue();
		}

		if (this.args.size() == 1) {
			this.value = null;
			this.type = void.class;
			return;
		}

		Action action = new Action() {
			@Override
			public boolean beforeRun(Form form) {
				form.setDebug(true);
				return true;
			}

			@Override
			public boolean afterRun(Form form) {
				form.setDebug(false);
				return true;
			}
		};

		Form form;
		for (String formId : this.args.subList(1, this.args.size())) {
			form = this.inCode.getFormMap().get(formId);
			if (form != null) {
				runFormSeq(form.getFormSeqWhichRunBy(this), action);
			}
		}

		Main.debug=mainDebug;
		for (Object elem : rollback.keySet()) {
			if(elem instanceof Sub){
				((Sub)elem).setIsDebug(rollback.get(elem));
			}else{
				Main.codes.get(elem).setDebug(rollback.get(elem));
			}

		}
	}
}
