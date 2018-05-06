package lava.core.keyword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lava.Main;
import lava.core.DataMap.Data;
import lava.core.Form;
import lava.core.Sub;
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
		
		if(this.args.size() == 0){
			this.type=resources.getClass();
			this.value=resources;
			return;
		}
		
		resources.putAll(Main.jarClass);
	
		for (String key : resources.keySet()) {
			boolean isMatched = true;
			boolean isGoodMatched = false;
			List<String> fieldPattern = null;
			for (Data data : this.parseFormArgs(this.args)) {
				String pattern = String.valueOf(data.getValue());
				if (fieldPattern != null) {
					fieldPattern.add(pattern);
				}

				if (!key.toUpperCase()
						.contains(pattern.toUpperCase())) {
					isMatched = false;
					break;
				}
				if (key.equals(pattern)) {
					isGoodMatched = true;
					fieldPattern = new ArrayList<String>();
				}
			}

			if (isGoodMatched) {
				result.put(key,
						getMethodList(fieldPattern, resources.get(key)));
			} else if (isMatched) {
				result.put(key, resources.get(key));
			}
		}
		
		this.type = result.getClass();
		this.value = result;
		return;

	}

	private List getMethodList(List<String> fieldPattern, Object clazz) {
		if (clazz == null || !(clazz instanceof Class)) {
			return null;
		}

		List<Object> list = new ArrayList<Object>();
		list.addAll(Arrays.asList(((Class)clazz).getDeclaredMethods()));
		list.addAll(Arrays.asList(((Class)clazz).getDeclaredFields()));

		List<Object> result = new ArrayList<Object>();

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
