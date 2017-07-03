package lava.constant;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import lava.core.keyword.AndForm;
import lava.core.keyword.AsForm;
import lava.core.keyword.AssignForm;
import lava.core.keyword.DebugForm;
import lava.core.keyword.DefForm;
import lava.core.keyword.EvalForm;
import lava.core.keyword.ForForm;
import lava.core.keyword.ForeachForm;
import lava.core.keyword.IfForm;
import lava.core.keyword.LoadForm;
import lava.core.keyword.NewForm;
import lava.core.keyword.OrForm;
import lava.core.keyword.QDefForm;
import lava.core.keyword.QEqForm;
import lava.core.keyword.ReturnForm;
import lava.core.keyword.TypeForm;
import lava.core.keyword.UndefForm;
import lava.core.keyword.UseForm;
import lava.core.keyword.WhileForm;

@SuppressWarnings("rawtypes")
public class Constants {
	public static String					newLine				= System.getProperty("line.separator", "\n");

	public static String					idMid				= "@";

	public static String					formIdSuffix		= idMid + "F";
	public static String					stringIdSuffix		= idMid + "S";
	public static String					numberIdSuffix		= idMid + "N";

	public static Map<String, Class>		keywords			= new HashMap<String, Class>();
	public static Map<String, Class>		baseTypes			= new HashMap<String, Class>();

	public static Map<String, Class>		numberTypes			= new HashMap<String, Class>();
	public static Map<String, Constructor>	numberParses		= new HashMap<String, Constructor>();

	public static String					subPrefix			= "/";
	public static String					javaChar			= ".";
	public static String					gsChar				= ":";

	public static String					configSplit			= "=";
	public static String					empty				= "";
	public static String					privatePrefix		= "-";
	public static String					privateSubPrefix	= subPrefix + "-";
	public static String					systemVarPrefix		= "$";

	public static String					replPrefix			= ">";

	static {
		keywords.put("load", LoadForm.class);
		keywords.put("use", UseForm.class);
		keywords.put("as", AsForm.class);
		keywords.put("type", TypeForm.class);
		keywords.put("def", DefForm.class);
		keywords.put("=", AssignForm.class);
		keywords.put("==", QEqForm.class);
		keywords.put("new", NewForm.class);
		keywords.put("if", IfForm.class);
		keywords.put("for", ForForm.class);
		keywords.put("while", WhileForm.class);
		keywords.put("def?", QDefForm.class);
		keywords.put("foreach", ForeachForm.class);
		keywords.put("undef", UndefForm.class);
		keywords.put("return", ReturnForm.class);
		keywords.put("||", OrForm.class);
		keywords.put("&&", AndForm.class);
		keywords.put("debug", DebugForm.class);
		keywords.put("eval", EvalForm.class);
	}

	static {
		baseTypes.put("int", int.class);
		baseTypes.put("short", short.class);
		baseTypes.put("byte", byte.class);
		baseTypes.put("char", char.class);
		baseTypes.put("long", long.class);
		baseTypes.put("float", float.class);
		baseTypes.put("double", double.class);
		baseTypes.put("boolean", boolean.class);
	}

	static {
		numberTypes.put("i", int.class);
		numberTypes.put("l", long.class);
		numberTypes.put("f", float.class);
		numberTypes.put("d", double.class);
		numberTypes.put("I", Integer.class);
		numberTypes.put("L", Long.class);
		numberTypes.put("F", Float.class);
		numberTypes.put("D", Double.class);
	}

	static {
		Constructor newInteger = null;
		Constructor newLong = null;
		Constructor newFloat = null;
		Constructor newDouble = null;
		try {
			newInteger = Integer.class.getConstructor(String.class);
			newLong = Long.class.getConstructor(String.class);
			newFloat = Float.class.getConstructor(String.class);
			newDouble = Double.class.getConstructor(String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		numberParses.put("i", newInteger);
		numberParses.put("l", newLong);
		numberParses.put("f", newFloat);
		numberParses.put("d", newDouble);
		numberParses.put("I", newInteger);
		numberParses.put("L", newLong);
		numberParses.put("F", newFloat);
		numberParses.put("D", newDouble);
	}

}
