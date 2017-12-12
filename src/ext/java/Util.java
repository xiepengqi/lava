package ext.java;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lava.Main;
import lava.util.StringUtil;

public class Util {

	public static int compare(String a,String b){
		return new BigDecimal(a).compareTo(new BigDecimal(b));
	}

	public static int add(int a, int b) {
		return a + b;
	}

	public static int sub(int a, int b) {
		return a - b;
	}

	public static int mul(int a, int b) {
		return a * b;
	}

	public static int div(int a, int b) {
		return a / b;
	}

	public static double add(double a, double b) {
		return a + b;
	}

	public static double sub(double a, double b) {
		return a - b;
	}

	public static double mul(double a, double b) {
		return a * b;
	}

	public static double div(double a, double b) {
		return a / b;
	}

	public static float add(float a, float b) {
		return a + b;
	}

	public static float sub(float a, float b) {
		return a - b;
	}

	public static float mul(float a, float b) {
		return a * b;
	}

	public static float div(float a, float b) {
		return a / b;
	}

	public static long add(long a, long b) {
		return a + b;
	}

	public static long sub(long a, long b) {
		return a - b;
	}

	public static long mul(long a, long b) {
		return a * b;
	}

	public static long div(long a, long b) {
		return a / b;
	}

	public static String add(String a, String b) {
		return new BigDecimal(a).add(new BigDecimal(b)).toString();
	}

	public static String sub(String a, String b) {
		return new BigDecimal(a).subtract(new BigDecimal(b)).toString();
	}

	public static String mul(String a, String b) {
		return new BigDecimal(a).multiply(new BigDecimal(b)).toString();
	}

	public static String div(String a, String b) {
		return new BigDecimal(a).divide(new BigDecimal(b)).toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map parseListToMap(List list) {
		Map map = new HashMap();
		if (list == null || list.size() == 0) {
			return map;
		}
		for (int i = 0; i < list.size(); i += 2) {
			map.put(list.get(i), list.size() == i + 1 ? null : list.get(i + 1));
		}

		return map;
	}

	@SuppressWarnings("rawtypes")
	public static boolean aInstanceOfList(Object a, Class b) {
		return a instanceof List;
	}

	public static String readFile(String filePath) throws IOException {
		return lava.util.FileUtil.readFile(new File(filePath));
	}

	@SuppressWarnings("rawtypes")
	public static List<List> findStrs(String str, String re) {
		List<List> result = new ArrayList<List>();

		Pattern p = Pattern.compile(re);
		Matcher matcher = p.matcher(str);
		List<String> list = null;
		while (matcher.find()) {
			list = new ArrayList<String>();
			result.add(list);

			list.add(matcher.group());
			if (matcher.groupCount() == 0) {
				break;
			}

			for (int i = 1; i <= matcher.groupCount(); i++) {
				list.add(matcher.group(i));
			}
		}
		return result;
	}

	public static void main(String[] args) {
		Pattern p = Pattern.compile("xie(\\d+)(.*)");
		Matcher matcher = p.matcher("xie1peng");
		matcher.find();
		System.out.println(matcher.groupCount());

		System.out.println(matcher.group());
		System.out.println(matcher.group(1));
	}

	@SuppressWarnings("rawtypes")
	public static List<String> split(String re, Object... objects) {
		List<String> list = new ArrayList<String>();

		for (Object obj : objects) {
			if (obj instanceof List) {
				for (Object o : (List) obj) {
					String[] strs = o.toString().split(re);
					list.addAll(Arrays.asList(strs));
				}
			} else {
				String[] strs = obj.toString().split(re);
				list.addAll(Arrays.asList(strs));
			}
		}
		List<String> result = new ArrayList<String>();
		for (String str : list) {
			if (!StringUtil.isEmpty(str)) {
				result.add(str);
			}
		}

		return result;
	}

	public static List<Object> lvar(String codeId){
		List list=new ArrayList();
		list.addAll(Arrays.asList(Main.codes.get(codeId).getDataMap().getMap().keySet().toArray()));
		return list;
	}

}
