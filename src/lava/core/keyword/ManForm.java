package lava.core.keyword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lava.Main;
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

		if (this.args.size() > 1) {
			resources.putAll(Main.jarClass);
		}

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
				List<String> methodPatternList = pattern == null ? null : StringUtil.validSplit(pattern, "\\s+");
				
				Object obj= getClassItemList(methodPatternList, resources.get(key));
				if((patternList.size() == 0 && 
						methodPatternList !=null && 
						methodPatternList.size() > 0 &&
						((!(obj instanceof Collection)) ||
							((Collection)obj).size() == 0))){
					continue;
				} 
				
				result.put(key, obj);
			}
		}

		this.type = result.getClass();
		this.value = result;
	}

	private Object getClassItemList(List<String> fieldPattern, Object clazz) {
		if (clazz == null || !(clazz instanceof Class)) {
			return clazz;
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
