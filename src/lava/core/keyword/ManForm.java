package lava.core.keyword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lava.Main;
import lava.constant.Constants;
import lava.core.Form;
import lava.core.Sub;
import lava.util.StringUtil;
import lava.util.Util;

public class ManForm extends Form {
	@Override
	public void parse() {
		super.parse();
	}

	@Override
	public void check() {
		super.check();
	}

	@Override
	public void run() throws Exception {
		super.run();

		Map<String, Object> resources = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();

		Util.Action action = new Util.Action() {
			public boolean isOverAble() {
				return false;
			}
		};

		for (Sub sub : this.inSubSeq) {
			Util.putAll(sub.getDataMap().getMap(), resources, action);
			Util.putAll(sub.getClosure().getMap(), resources, action);
		}

		Util.putAll(this.inCode.getDataMap().getMap(), resources, action);

		if (this.args.size() == 0) {
			this.type = resources.getClass();
			this.value = resources;
			return;
		}

		resources.putAll(Main.jarClass);

		for (String key : resources.keySet()) {
			List<String> patternList = StringUtil.validSplit((String) this
					.parseFormArg(this.args.get(0)).getValue(), "\\s+");
			boolean isMatched = true;
			for (String pattern : patternList) {
				if (!key.toUpperCase().contains(pattern.toUpperCase())) {
					isMatched = false;
					break;
				}
			}

			if (isMatched) {
				String pattern = this.args.size() > 1 ? (String) this
						.parseFormArg(this.args.get(1)).getValue()
						: null;
				patternList = pattern == null ? null : StringUtil.validSplit(pattern, "\\s+");
				result.put(key, getMethodList(patternList, resources.get(key)));
			}
		}

		this.type = result.getClass();
		this.value = result;
		return;

	}

	private Object getMethodList(List<String> fieldPattern, Object clazz) {
		if (clazz == null || !(clazz instanceof Class)) {
			return clazz;
		}

		List<Object> list = new ArrayList<Object>();
		list.addAll(Arrays.asList(((Class) clazz).getDeclaredMethods()));
		list.addAll(Arrays.asList(((Class) clazz).getDeclaredFields()));

		List<Object> result = new ArrayList<Object>();

		if(fieldPattern == null){
			return result;
		}
		
		for (Object obj : list) {
			boolean isMatched = true;

			for (String pattern : fieldPattern) {
				if (!String.valueOf(obj).toUpperCase()
						.contains(pattern.toUpperCase())) {
					isMatched = false;
					break;
				}
			}
			if (isMatched) {
				result.add(obj);
			}
		}

		return result;
	}

}
