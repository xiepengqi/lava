package lava.constant;

import lava.util.StringUtil;

public class RegexConstants {

	public static String	numberSuffix	= "[ilfdILFD]";
	public static String	number			= "-?\\d+\\.?\\d*" + numberSuffix + "?";

	public static String	defVar				= "[^0-9" + StringUtil.escapeReg(Constants.avoidsChars)+ "][^" + StringUtil.escapeReg(Constants.avoidsChars) + "]*";
	public static String	defFunc				= "[^0-9" +
			StringUtil.escapeReg(Constants.avoidsChars.replace(Constants.expand, ""))
			+ "][^" + StringUtil.escapeReg(Constants.avoidsChars.replace(Constants.expand, "")) + "]*";
	public static String	dataMapKey		= "[^0-9" +
			StringUtil.escapeReg(Constants.avoidsChars.replace(Constants.subPrefix, ""))
			+ "][^" + StringUtil.escapeReg(Constants.avoidsChars) + "]*";

	public static String	elemLeftBorder	= "(?<=[\\s"+StringUtil.escapeReg(Constants.leftBorder)+"]|^)";
	public static String	elemRightBorder	= "(?=[\\s"+StringUtil.escapeReg(Constants.rightBorder)+"]|$)";

	public static String    extractLString = "`[^`]*`";
	public static String    extractLStringVar = "\\{[^{}]+\\}";
	public static String	extractString	= "'[^']*'|\"[^\"]*\"";
	public static String	extractNumber		= elemLeftBorder + RegexConstants.number + elemRightBorder;

	public static String	extractForm;
	static {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		char[] leftChars = Constants.leftBorder.toCharArray();
		char[] rightChars = Constants.rightBorder.toCharArray();
		for (int i = 0; i < leftChars.length; i++) {
			sb.append(StringUtil.escapeReg(String.valueOf(leftChars[i])));
			sb.append("[^"+StringUtil.escapeReg(Constants.border)+"]*");
			sb.append(StringUtil.escapeReg(String.valueOf(rightChars[i])));
			if (i < leftChars.length - 1) {
				sb.append("|");
			}
		}
		sb.append(")");
		extractForm = sb.toString();
	}

	public static String	formBorder		= "^["+StringUtil.escapeReg(Constants.leftBorder)+"]|["+StringUtil.escapeReg(Constants.rightBorder)+"]$";

	public static String	formId			= "\\d+" + Constants.formIdSuffix;
	public static String	stringId		= "\\d+" + Constants.stringIdSuffix;
	public static String	numberId		= "\\d+" + Constants.numberIdSuffix;

	public static String	extractFormId	= elemLeftBorder + formId + elemRightBorder;
	public static String	extractNumberId	= elemLeftBorder + numberId + elemRightBorder;
	public static String	extractStringId	= elemLeftBorder + stringId + elemRightBorder;
	
}
