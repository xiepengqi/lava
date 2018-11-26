package lava.core.keyword;

import lava.Main;
import lava.core.Form;
import lava.core.Sub;
import lava.util.StringUtil;
import lava.util.Util;

import java.util.*;

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
		String pattern1 = (String) this.parseFormArg(this.args.get(0)).getValue();
		String pattern2 = null;
		if (this.args.size() > 1) {
			resources.putAll(Main.jarClass);
			pattern2 =  (String) this.parseFormArg(this.args.get(1)).getValue();
		}
		boolean blank2 = StringUtil.isBlank(pattern2);
		for (String key : resources.keySet()) {
			if ((blank2 && key.toUpperCase().contains(pattern1.toUpperCase())) || key.matches(pattern1)) {
				if(blank2){
					result.put(key, resources.get(key));
					continue;
				}
				List methods = getClassItemList(pattern2, resources.get(key));
				if (methods.size() > 0) {
					result.put(key, methods);
				}
			}
		}

		this.type = result.getClass();
		this.value = result;
	}

	private List<Object> getClassItemList(String fieldPattern, Object clazz) {
		List<Object> result = new ArrayList<Object>();
		if (StringUtil.isBlank(fieldPattern) || clazz == null || !(clazz instanceof Class)) {
			return result;
		}

		List<Object> list = new ArrayList<Object>();
		try {
			while (clazz !=null) {
				list.addAll(Arrays.asList(((Class) clazz).getDeclaredMethods()));
				list.addAll(Arrays.asList(((Class) clazz).getDeclaredFields()));
				list.addAll(Arrays.asList(((Class) clazz).getDeclaredConstructors()));
				clazz = ((Class) clazz).getSuperclass();
			}
		} catch (Throwable ignored){}

		for (Object obj : list) {
			if (String.valueOf(obj).matches(fieldPattern)) {
				result.add(obj);
			}
		}

		return result;
	}

}
