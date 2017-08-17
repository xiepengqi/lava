package lava.core.keyword;

import java.util.ArrayList;
import java.util.List;

import lava.constant.Constants;
import lava.core.DataMap;
import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.Sub;
import lava.util.StringUtil;
import lava.util.Util;

public class FnForm extends Form {
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
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);
		Sub sub = getSubFromScope();
		if (null != sub) {
			for (int i = 0; i < parseArgs.size(); i++) {
				sub.getDataMap().put("$" + i, parseArgs.get(i));
				sub.getDataMap().put("$-" + (parseArgs.size() - i), parseArgs.get(i));
			}

			List<Object> values = new ArrayList<Object>();
			Util.splitArgs(parseArgs, values, null);
			sub.getDataMap().put("$args", values);
			sub.run();
			this.value = sub.getAsForm().getValue();
			this.type = sub.getAsForm().getType();
			return;
		} else {
			Util.runtimeError(this, this.fnName);
		}
	}

	private Sub getSubFromScope() {
		if (StringUtil.isFormId(this, this.fnName)) {
			Object obj = this.inCode.getFormMap().get(this.fnName).getValue();
			if (obj instanceof Sub) {
				return (Sub) obj;
			}
		}

		String subNameInDataMap = Constants.subPrefix + this.fnName;
		Sub findSub;
		for (Sub sub : this.inSubSeq) {
			findSub = findSub(sub.getDataMap(), subNameInDataMap);
			if (null != findSub) {
				return findSub;
			}
			findSub = findSub(sub.getClosure(), subNameInDataMap);
			if (null != findSub) {
				return findSub;
			}
		}

		findSub = findSub(this.inCode.getDataMap(), subNameInDataMap);
		if (null != findSub) {
			return findSub;
		}
		return null;
	}

	public Sub findSub(DataMap dataMap, String subNameInDataMap) {
		DataInfo data = dataMap.get(subNameInDataMap);
		if (null == data) {
			data = dataMap.get(this.fnName);
		}

		if (data != null && data.getValue() instanceof Sub) {
			return (Sub) data.getValue();
		}
		return null;
	}
}
