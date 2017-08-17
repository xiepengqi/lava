package lava.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lava.constant.Constants;
import lava.constant.RegexConstants;
import lava.core.Form;

public class StringUtil {
	public static boolean isInnerId(Form form, String str) {
		if (isFormId(form, str)) {
			return true;
		}
		if (isNumberId(form, str)) {
			return true;
		}
		if (isStringId(form, str)) {
			return true;
		}
		return false;
	}

	public static boolean isDataMapKeyAble(String str) {
		if (!str.matches(RegexConstants.dataMapKey)) {
			return false;
		}
		for (String key : Constants.keywords.keySet()) {
			if (key.equals(str)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isFnNameAble(String str) {

		return false;
	}

	public static boolean isNotInnerId(Form form, String str) {
		return !isInnerId(form, str);
	}

	public static boolean isFormId(Form form, String str) {
		if (null == form.getInCode().getFormMap().get(str)) {
			return false;
		}
		return true;
	}

	public static boolean isNotFormId(Form form, String str) {
		return !isFormId(form, str);
	}

	public static boolean isStringId(Form form, String str) {
		if (null == form.getInCode().getStringMap().get(str)) {
			return false;
		}
		return true;
	}

	public static boolean isNotStringId(Form form, String str) {
		return !isStringId(form, str);
	}

	public static boolean isNumberId(Form form, String str) {
		if (null == form.getInCode().getNumberMap().get(str)) {
			return false;
		}
		return true;
	}

	public static boolean isNotNumberId(Form form, String str) {
		return !isNumberId(form, str);
	}

	public static boolean isVarAble(String str) {
		if (!str.matches(RegexConstants.var)) {
			return false;
		}
		for (String key : Constants.keywords.keySet()) {
			if (key.equals(str)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isKeyWords(String str) {
		for (String key : Constants.keywords.keySet()) {
			if (key.equals(str)) {
				return true;
			}
		}

		return false;
	}

	public static String getFirstMatch(String re, String source) {
		Pattern p = Pattern.compile(re);
		Matcher matcher = p.matcher(source);
		while (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	public static String getFirstForm(String source) {
		return getFirstMatch(RegexConstants.extractForm, source);
	}

	public static String replaceFirstForm(String rep, String source) {
		return source.replaceFirst(RegexConstants.extractForm, rep);
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean isEmpty(String str) {
		if (null != str && str.length() > 0) {
			return false;
		} else {
			return true;
		}
	}

}
