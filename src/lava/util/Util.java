package lava.util;

import lava.Main;
import lava.constant.Constants;
import lava.constant.RegexConstants;
import lava.core.Code;
import lava.core.DataMap.Data;
import lava.core.Form;
import lava.core.Sub;

import java.util.*;

public class Util {
	public static int debug_when_form_begin =0;
	public static int debug_when_form_end =1;


	public static String seeNumber(Code inCode, String seeSource) {
		String temp = null;
		String innerId = StringUtil.getFirstMatch(RegexConstants.extractNumberId, seeSource);
		while (innerId != null) {
			temp = inCode.getNumberMap().get(innerId).getSource();
			temp = java.util.regex.Matcher.quoteReplacement(temp);
			seeSource = seeSource.replaceFirst(RegexConstants.extractNumberId, temp);
			innerId = StringUtil.getFirstMatch(RegexConstants.extractNumberId, seeSource);
		}
		return seeSource;
	}

	public static String seeString(Code inCode, String seeSource) {
		String temp = null;
		String innerId = StringUtil.getFirstMatch(RegexConstants.extractStringId, seeSource);

		List<String> sourceList = new ArrayList<String>();
		List<String> stringList = new ArrayList<String>();
		while (innerId != null) {
			String[] strs = seeSource.split(innerId);
			sourceList.add(strs[0]);
			seeSource = strs[1];

			temp = inCode.getStringMap().get(innerId).getSource();
			stringList.add(temp);

			innerId = StringUtil.getFirstMatch(RegexConstants.extractStringId, seeSource);
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < stringList.size(); i++) {
			sb.append(sourceList.get(i));
			sb.append(stringList.get(i));
		}
		sb.append(seeSource);
		return sb.toString();
	}

	public static class Action {
		public boolean isRemoveAble(Object o) {
			return false;
		}

		public boolean isPutAble(Object useKey) {
			return true;
		}

		public boolean isOverAble() {
			return true;
		}

		public Object defToKey(Object useKey) {
			return null;
		}

		public Iterable<Object> defUseKeys() {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static void splitArgs(List<Data> args, List<Object> values, List<Class> types) {
		for (Data arg : args) {
			if (values != null)
				values.add(arg.getValue());
			if (types != null)
				types.add(arg.getType());
		}
	}

	public static <T> void unique(List<T> paths, Action action) {
		Set<T> set = new LinkedHashSet<T>(paths);

		paths.clear();
		for (T path : set) {
			if (action.isRemoveAble(path)) {
				continue;
			}
			paths.add(path);
		}
	}

	public static void syntaxError(Form form, String str) {
		System.err.println("SYNTAX-ERROR:" + form.getWhere() + ":" + form.see() + ":" + str);
		Main.syntaxError = true;
	}

	public static void runtimeError(Code code, String str) {
		System.err.println("RUNTIME-ERROR:" + code.getIdName() + ":" + str);
		if (!Main.repl) {
			System.exit(1);
		}
	}
	public static void runtimeError(String str) {
		System.err.println("RUNTIME-ERROR:" + str);
		if (!Main.repl) {
			System.exit(1);
		}
	}

	public static void debug(Form form, int debugMode) {
		if(!isDebug(form)){
			return;
		}
		String msg=Constants.empty;
		if(debugMode == debug_when_form_begin){
			msg=StringUtil.join(Constants.sepOrObjChar,form.getFormId(),form.see());
		}
		if(debugMode == debug_when_form_end){
			msg=StringUtil.join(Constants.sepOrObjChar,form.getFormId(),form.getType(),form.getValue());
		}

		System.out.println(StringUtil.join(Constants.sepOrObjChar,form.getWhere(),msg));
	}

	public static boolean isDebug(Form form){
		boolean subDebug=false;
		for (Sub sub : form.getInSubSeq()) {
			if(sub.isDebug()){
				subDebug=true;
			}
		}

		return form.isDebug() || form.getInCode().isDebug()||
				Main.debug||subDebug;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void putAll(Map useMap, Map toMap, Action action) {

		Iterable<Object> useKeys = action.defUseKeys();

		if (useKeys == null) {
			useKeys = useMap.keySet();
		}
		for (Object useKey : useKeys) {
			if (!action.isPutAble(useKey)) {
				continue;
			}

			Object toKey = useKey;
			Object temp = action.defToKey(useKey);
			if (null != temp) {
				toKey = temp;
			}

			if (toMap.containsKey(toKey) && !action.isOverAble()) {
				continue;
			}
			toMap.put(toKey, useMap.get(useKey));
		}
	}

}
