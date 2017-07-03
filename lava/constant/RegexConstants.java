package lava.constant;

public class RegexConstants {

	public static String	numberSuffix	= "[ilfdILFD]";
	public static String	number			= "-?\\d+\\.?[\\d+]?" + numberSuffix + "?";

	public static String	avoidsChars		= Constants.javaChar + Constants.subPrefix + Constants.gsChar;
	static {
		avoidsChars = avoidsChars.replaceAll("\\.", "\\\\.");
	}

	public static String	var				= "[^0-9" + avoidsChars + "][^" + avoidsChars + "]*";

	public static String	dataMapKey		= var;

	static {
		dataMapKey = dataMapKey.replaceFirst(Constants.subPrefix, "");
	}

	public static String	elemLeftBorder	= "(?<=[:\\s\\(\\[\\{]|^)";
	public static String	elemRightBorder	= "(?=[:\\s\\)\\]\\}]|$)";

	public static String	extractString	= "`[^`]*`|'[^']*'|\"[^\"]*\"";
	public static String	extractNumber		= elemLeftBorder + RegexConstants.number + elemRightBorder;
	public static String	extractForm		= "(" 
				+ "\\([^\\(\\)\\[\\]\\{\\}]*\\)|" 
				+ "\\[[^\\(\\)\\[\\]\\{\\}]*\\]|"
				+ "\\{[^\\(\\)\\[\\]\\{\\}]*\\}" 
				+ ")";

	public static String	formBorder		= "^[\\(\\[\\{]|[\\)\\]\\}]$";

	public static String	formId			= "\\d+" + Constants.formIdSuffix;
	public static String	stringId		= "\\d+" + Constants.stringIdSuffix;
	public static String	numberId		= "\\d+" + Constants.numberIdSuffix;

	public static String	extractFormId	= elemLeftBorder + formId + elemRightBorder;
	public static String	extractNumberId	= elemLeftBorder + numberId + elemRightBorder;
	public static String	extractStringId	= elemLeftBorder + stringId + elemRightBorder;
	
}
