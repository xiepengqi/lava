package lava.constant;

import lava.util.StringUtil;

public class RegexConstants {

	public static String	numberSuffix	= "[ilfdILFD]";
	public static String	number			= "-?\\d+\\.?\\d*" + numberSuffix + "?";

	public static String	defVar				= "[^0-9" + StringUtil.escapeReg(Constants.avoidsChars)+ "][^" + Constants.avoidsChars + "]*";

	public static String	dataMapKey		= "[^0-9" + StringUtil.escapeReg(Constants.avoidsChars.replace(Constants.subPrefix, ""))+ "][^" + Constants.avoidsChars + "]*";

	public static String	elemLeftBorder	= "(?<=[\\s\\(\\[\\{]|^)";
	public static String	elemRightBorder	= "(?=[\\s\\)\\]\\}]|$)";

	public static String    extractLString = "`[^`]*`";
	public static String    extractLStringVar = "\\{[^{}]+\\}";
	public static String	extractString	= "'[^']*'|\"[^\"]*\"";
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
