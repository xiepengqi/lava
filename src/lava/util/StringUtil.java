package lava.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lava.constant.Constants;
import lava.constant.RegexConstants;
import lava.core.Form;

public class StringUtil {
	
	public static String toFmtString(Object obj, int... levelArray){
		int level = 0;
		if(levelArray !=null && levelArray.length > 0){
			level = levelArray[0];
		}
		
		StringBuilder sb=new StringBuilder();
		if(obj instanceof Map){
			sb.append("{");
			for(Object key:((Map)obj).keySet()){
				sb.append(Constants.newLine);
				sb.append(toFmtString( key, level+1) + " : " + toFmtString(((Map)obj).get(key), level+1).trim());
			}
			if(sb.length() > 1){
				sb.append(Constants.newLine + toFmtString("}", level));
			} else {
				sb.append("}");
			}
			
		}else if(obj instanceof Iterable){
			sb.append("[");
			for(Object item:(Iterable)obj){
				sb.append(Constants.newLine);
				sb.append(toFmtString(item, level+1));
			}
			if(sb.length() > 1){
				sb.append(Constants.newLine + toFmtString("]", level));
			} else {
				sb.append("]");
			}
		}else if(obj !=null && obj.getClass().isArray()){
			String value=null;
			
			if(obj instanceof int[]){
				value=Arrays.toString((int[]) obj);
			}else if(obj instanceof float[]){
				value=Arrays.toString((float[]) obj);
			}else if(obj instanceof long[]){
				value=Arrays.toString((long[]) obj);
			}else if(obj instanceof double[]){
				value=Arrays.toString((double[]) obj);
			}else if(obj instanceof char[]){
				value=Arrays.toString((char[]) obj);
			}else if(obj instanceof byte[]){
				value=Arrays.toString((byte[]) obj);
			}else if(obj instanceof short[]){
				value=Arrays.toString((short[]) obj);
			}else if(obj instanceof boolean[]){
				value=Arrays.toString((boolean[]) obj);
			}else{
				value=Arrays.deepToString((Object[]) obj);
			}
			sb.append(value);
		}else{
			sb.append(getBlank(level * 4));
			sb.append(obj);
		}

		return sb.toString();
	}
	
	public static String getBlank(int num){
		StringBuilder sb=new StringBuilder();
		for(int i = 0;i<num;i++){
			sb.append(" ");
		}
		return sb.toString();
	}
	
	public static String toString(Object obj){
		return obj==null ? "":obj.toString();
	}

	public static List<String> validSplit(String source,String flag){
		String[] strs=toString(source).split(toString(flag));
		List<String> list=new ArrayList<String>();
		
		for(String str:strs){
			if(isNotBlank(str)){
				list.add(str);
			}
		}
		return list;
	}
	
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

	public static String join(String sep,Object... objs){
		if(objs.length==0){
			return "";
		}
		StringBuilder sb=new StringBuilder();
		boolean flag=false;
		for (Object obj : objs) {
			if(flag){
				sb.append(sep);
			}
			flag=true;
			sb.append(String.valueOf(obj));
		}
		return sb.toString();
	}

	public static boolean isDataMapKeyAble(String str) {
		if (!str.matches(RegexConstants.dataMapKey)) {
			return false;
		}
		if(isKeyWords(str)){
			return false;
		}
		return true;
	}

	public static boolean isFormId(Form form, String str) {
		if (null == form.getInCode().getFormMap().get(str)) {
			return false;
		}
		return true;
	}


	public static boolean isStringId(Form form, String str) {
		if (null == form.getInCode().getStringMap().get(str)) {
			return false;
		}
		return true;
	}

	public static boolean isNumberId(Form form, String str) {
		if (null == form.getInCode().getNumberMap().get(str)) {
			return false;
		}
		return true;
	}

	public static boolean isDefVarAble(String str) {
		if (!str.matches(RegexConstants.defVar)) {
			return false;
		}
		if(isKeyWords(str)){
			return false;
		}
		return true;
	}

	public static boolean isFnAble(String str) {
		if (StringUtil.isBlank(str)) {
			return true;
		}
		if (!str.matches(RegexConstants.defFunc)) {
			return false;
		}
		if (isKeyWords(str)){
			return false;
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

	public static List<String> getMatchers(String regex, String source){  
	    Pattern pattern = Pattern.compile(regex);  
	    Matcher matcher = pattern.matcher(source);  
	    List<String> list = new ArrayList<String>();  
	    while (matcher.find()) {  
	        list.add(matcher.group());  
	    }  
	    return list;  
	}  
	/** 
	 * 转义正则特殊字符 （$()*+.[]?\^{},|） 
	 *  
	 * @param keyword 
	 * @return 
	 */  
	public static String escapeReg(String str) {
		if(isBlank(str)){
			return str;
		}
		String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?",
				"^", "{", "}", "|" };
		for (String key : fbsArr) {
			if (str.contains(key)) {
				str = str.replace(key, "\\" + key);
			}
		}
		return str;
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
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static boolean isBlank(String str) {
		if (null != str && str.trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}

}
