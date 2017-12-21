package lava.core.keyword;

import lava.constant.Constants;
import lava.core.DataMap;
import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.util.StringUtil;
import lava.util.Util;

public class DefForm extends Form {
	@Override
	public void parse() {

	}

	@Override
	public void check() {
		super.check();

		for(String arg:this.args.subList(0,this.args.size()-1)){
			if(arg.startsWith(Constants.systemVarPrefix)||!StringUtil.isVarAble(arg)){
				Util.syntaxError(this,arg);
			}
		}
	}

	@Override
	public void run() throws Exception {
		super.run();
		processFormDef();
	}

	private void processFormDef() {
		DataMap dataMap = null;
		if (inSubSeq.size() > 0) {
			dataMap = this.inSubSeq.get(0).getDataMap();
		} else {
			dataMap = this.inCode.getDataMap();
		}

		DataInfo data = null;
		if (this.args.size() < 2) {
			if (dataMap.getMap().containsKey(this.args.get(0))) {
				Util.runtimeError(this, this.args.get(0));
			}

			data = new DataInfo();
			data.setValue(null);
			data.setType(void.class);
			data.setSource(this.getSource());
			dataMap.putData(this.args.get(0), data);

			this.value = null;
			this.type = void.class;
			return;
		}

		String arg = this.args.get(this.args.size() - 1);
		DataInfo value = parseFormArg(arg);

		for (String var : this.args.subList(0, this.args.size() - 1)) {
			if (dataMap.getMap().containsKey(var)) {
				Util.runtimeError(this, var);
			} else {
				data = new DataInfo();
				data.setValue(value.getValue());
				data.setType(value.getType());
				data.setIn(this.getInCode().getDataMap());
				data.setSource(this.getSource());
				dataMap.putData(var, data);
			}
		}

		this.value = value.getValue();
		this.type = value.getType();
	}
}
