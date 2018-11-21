package lava.constant;

import lava.core.keyword.*;
import lava.util.Util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class Constants {
	public static String					newLine				= System.getProperty("line.separator", "\n");

	public static Map<String, Class>		keywords			= new HashMap<String, Class>();
	public static Map<String, Class>		baseTypes			= new HashMap<String, Class>();
	public static Map<String, Class>		numberTypes			= new HashMap<String, Class>();
	public static Map<String, Constructor>	numberParses		= new HashMap<String, Constructor>();

	public static String					subPrefix			= "/";
	public static String					javaChar			= ".";
	public static String					idMid				= "@";
	public static String					systemVarPrefix		= "$";
	public static String 					sepOrObjChar					= ":";
	public static String 					expand				="*";

	public static String	avoidsChars		=idMid + sepOrObjChar + javaChar +subPrefix + systemVarPrefix + expand;
	
	public static String					formIdSuffix		= idMid + "F";
	public static String					stringIdSuffix		= idMid + "S";
	public static String					numberIdSuffix		= idMid + "N";
	public static String					replPrefix			= ">";
	public static String					empty				= "";
	public static String					configSplit			= "=";
	
    static {
		keywords.put("load", LoadForm.class);
		keywords.put("use", UseForm.class);
		keywords.put("as", AsForm.class);
		keywords.put("type", TypeForm.class);
		keywords.put("def", DefForm.class);
		keywords.put("=", AssignForm.class);
		keywords.put("==", QSameForm.class);
		keywords.put("=?", QEqForm.class);
		keywords.put("new", NewForm.class);
		keywords.put("if", IfForm.class);
		keywords.put("for", ForForm.class);
		keywords.put("while", WhileForm.class);
		keywords.put("foreach", ForeachForm.class);
		keywords.put("throw", ThrowForm.class);
		keywords.put("return", ReturnForm.class);
		keywords.put("or", OrForm.class);
		keywords.put("and", AndForm.class);
		keywords.put("debug", DebugForm.class);
		keywords.put("eval", EvalForm.class);
		keywords.put("catch", CatchForm.class);
		keywords.put("man", ManForm.class);
		keywords.put("export", ExportForm.class);
		keywords.put("?", SwitchForm.class);
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
		} catch (Throwable t) {
			Util.systemError("lava start failed: " + t.toString());
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
