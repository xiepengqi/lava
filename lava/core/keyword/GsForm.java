package lava.core.keyword;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lava.constant.Constants;
import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.Sub;
import lava.util.Util;

public class GsForm extends Form {
	@Override
	public void parse() throws Exception {
		super.parse();

	}

	@Override
	public void check() {
		super.check();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void run() throws Exception {
		super.run();

		String[] keys = this.fnName.split(Constants.gsChar);

		Object main = parseFormArg(keys[0]).getValue();
		DataInfo keyArg = null;

		List<DataInfo> valueArgs = parseFormArgs(this.args);

		boolean isS = this.args.size() != 0;
		for (int i = 1; i < keys.length; i++) {
			keyArg = parseFormArg(keys[i]);
			boolean isLast = i == keys.length - 1;
			boolean isMap = main instanceof Map;
			boolean isList = main instanceof List;
			boolean isKeySub = keyArg.getValue() instanceof Sub;
			boolean isKeyMap = keyArg.getValue() instanceof Map;

			if (!(isMap || isList)) {
				Util.runtimeError(this, keys[i - 1]);
			}
			if (isKeySub) {
				Sub sub = (Sub) keyArg.getValue();
				List result = new ArrayList();
				Iterable it = null;
				if (isList) {
					it = (Iterable) main;
				} else {
					it = ((Map) main).entrySet();
				}

				for (Object obj : it) {
					List args = new ArrayList();
					args.add(obj);

					sub.getDataMap().put("$args", args);
					sub.getDataMap().put("$0", obj);
					sub.getDataMap().put("$-1", obj);

					sub.run();

					result.add(sub.getAsForm().getValue());
				}
				if (isLast) {
					this.value = result;
					this.type = List.class;
				}

				continue;
			}
			if (isKeyMap) {
				Map keyMap = (Map) keyArg.getValue();

				for (Object key : keyMap.keySet()) {
					Object value = keyMap.get(key);
					Object resultValue = null;
					if (value instanceof Sub) {
						Sub sub = (Sub) value;
						Object oriValue = isMap ? ((Map) main).get(key) : ((List) main).get(Integer.parseInt(key
								.toString()));
						List args = new ArrayList();
						args.add(oriValue);

						sub.getDataMap().put("$args", args);
						sub.getDataMap().put("$0", oriValue);
						sub.getDataMap().put("$-1", oriValue);

						sub.run();

						resultValue = sub.getAsForm().getValue();
					} else {
						resultValue = value;
					}
					if (isMap) {
						((Map) main).put(key, resultValue);
					} else {
						((List) main).set(Integer.parseInt(key.toString()), resultValue);
					}
				}

				continue;
			}

			if (isMap && isLast && isS) {
				((Map) main).put(keyArg.getValue(), valueArgs.get(0).getValue());
				this.value = valueArgs.get(0).getValue();
				this.type = valueArgs.get(0).getClass();
				continue;
			}

			if (isMap && isLast && !isS) {
				main = ((Map) main).get(keyArg.getValue());
				this.value = main;
				this.type = main == null ? void.class : main.getClass();
				continue;
			}

			if (isMap && !isLast) {
				main = ((Map) main).get(keyArg.getValue());
				continue;
			}

			if (isList && isLast && isS) {
				((List) main).set(Integer.parseInt(keyArg.getValue().toString()), valueArgs.get(0).getValue());
				this.value = valueArgs.get(0).getValue();
				this.type = valueArgs.get(0).getClass();
				continue;
			}

			if (isList && isLast && !isS) {
				main = ((List) main).get(Integer.parseInt(keyArg.getValue().toString()));
				this.value = main;
				this.type = main == null ? void.class : main.getClass();
				continue;
			}

			if (isList && !isLast) {
				main = ((List) main).get(Integer.parseInt(keyArg.getValue().toString()));
				continue;
			}

		}

	}

}
