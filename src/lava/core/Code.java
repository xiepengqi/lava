package lava.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

import lava.Main;
import lava.constant.Constants;
import lava.constant.RegexConstants;
import lava.util.FileUtil;
import lava.util.StringUtil;
import lava.util.Util;

public class Code {
	private String					filePath;
	private String 					packagePath;
	private String					idName;
	private DataMap					exports     = new DataMap();
	private List<Form>				formSeq		= new ArrayList<Form>();

	private DataMap					dataMap		= new DataMap();

	private Map<String, Form>		formMap		= new HashMap<String, Form>();
	private Map<String, Data>	stringMap	= new HashMap<String, Data>();
	private Map<String, Data>	numberMap	= new HashMap<String, Data>();
	private boolean	isReturn	= false;
	
	private Class type=void.class;
	private Object value=null;
	
	private boolean					isParsed;
	private boolean					isChecked;
	private boolean					isRuned;
	private boolean					debug;
	private String					source		= Constants.empty;

	public boolean isReturn() {
		return isReturn;
	}

	public void setReturn(boolean isReturn) {
		this.isReturn = isReturn;
	}

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public DataMap getExports() {
		return exports;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Map<String, Data> getNumberMap() {
		return numberMap;
	}

	public Map<String, Data> getStringMap() {
		return stringMap;
	}

	public Map<String, Form> getFormMap() {
		return formMap;
	}

	public Code(String packagePath, String filePath, String idName) {
		this.packagePath = packagePath;
		this.filePath = filePath;

		if(StringUtil.isNotBlank(idName)){
			this.idName = idName;
		} else {
			if (new File(packagePath).isDirectory()) {
				this.idName = filePath.substring(packagePath.length()+1)
						.replaceAll("\\.[^/\\.]+$", "")
						.replaceAll("[/\\\\]+",".");
			} else {
				this.idName = new File(packagePath).getName()
						.replaceFirst("\\.lava$", Constants.empty);
			}
		}

		for (String key : Main.config.keySet()) {
			dataMap.put(Constants.systemVarPrefix + key, Main.config.get(key));
		}

		dataMap.put("$ARGS", Main.ARGS);
		dataMap.put("$config", Main.config);
		dataMap.put("$codes", Main.codes);
		dataMap.put("$codeId", this.getIdName());
		dataMap.put("$subLinks", Main.subLinks);

		for(String key:Constants.baseTypes.keySet()){
			dataMap.put(key, Constants.baseTypes.get(key));
		}

	}

	public String getIdName() {
		return this.idName;
	}

	public DataMap getDataMap() {
		return dataMap;
	}

	public Map<String,Object> eval(String codeSource) throws Exception {
		int index = this.formSeq.size();

		codeSource = extractString(codeSource);
		codeSource = extractNumber(codeSource);
		codeSource = extractForm(codeSource);

		this.source += codeSource + Constants.newLine;

		List<Form> replFormSeq=this.formSeq.subList(index, this.formSeq.size());

		for (Form form : replFormSeq) {
			if (form.getInSubSeq().size() > 0) {
				form.getInSubSeq().get(0).getFormSeq().add(form);
			}
			form.check();
		}

		final Map<String,Object> data=new HashMap<String,Object>();

		Form.Action action=new Form.Action(){

			@Override
			public boolean beforeRun(Form form) {
				if (form.getInSubSeq().size() > 0) {
					return false;
				}
				if (null != form.getRunBy()) {
					return false;
				}
				return true;
			}

			@Override
			public boolean afterRun(Form form) {
				data.put("value",form.value);
				data.put("type",form.type);
				return true;
			}
		};

		Form.runFormSeq(replFormSeq,action);

		return data;
	}

	public void parse(){
		if (this.isParsed) {
			return;
		}
		this.isParsed = true;

		String codeSource = null;
		try {
			codeSource = FileUtil.readFile(this.filePath);
		} catch (Throwable t) {
			Util.systemError(this,t.toString());
		}

		codeSource = extractString(codeSource);
		codeSource = extractNumber(codeSource);
		codeSource = extractForm(codeSource);

		this.source = codeSource;
	}

	public void check() {
		if (this.isChecked) {
			return;
		}
		this.isChecked = true;

		for (Form form : this.formSeq) {
			if (form.getInSubSeq().size() > 0) {
				form.getInSubSeq().get(0).getFormSeq().add(form);
			}
			form.check();
		}
	}

	public void run() throws Exception {
		if (this.isRuned) {
			return;
		}
		this.isRuned = true;

		Form.Action action=new Form.Action(){
			@Override
			public boolean beforeRun(Form form) {
				if (form.getInSubSeq().size() > 0) {
					return false;
				}
				if (null != form.getRunBy()) {
					return false;
				}
				return true;
			}

			@Override
			public boolean afterRun(Form form) {
				return true;
			}
		};

		Form.runFormSeq(this.formSeq,action);

		Set keys = this.exports.getMap().keySet();
		this.type = Data.getClass(keys);
		this.value = keys;
	}

	private String extractForm(String codeSource){
		String formSource;
		int num = this.formMap.size();

		formSource = StringUtil.getFirstForm(codeSource);
		while (formSource != null) {
			String formId = num + Constants.formIdSuffix;
			Form form = Form.createdForm(formSource);
			form.setInCode(this);
			form.setSource(formSource);
			form.setFormId(formId);

			form.parse();

			this.formMap.put(formId, form);
			this.formSeq.add(form);

			codeSource = StringUtil.replaceFirstForm(formId, codeSource);
			formSource = StringUtil.getFirstForm(codeSource);

			num++;
		}
		return codeSource;
	}

	private String extractString(String codeSource) {
		codeSource=extractString(codeSource, RegexConstants.extractLString, LString.class);
		codeSource=extractString(codeSource, RegexConstants.extractString, String.class);
		
		return codeSource;
	}
	
	private String extractString(String codeSource, String reg, Class type){
		int num = this.stringMap.size();

		String stringSource = StringUtil.getFirstMatch(reg, codeSource);
		while (stringSource != null) {
			String stringId = num + Constants.stringIdSuffix;
			
			Object val = stringSource.substring(1, stringSource.length() - 1);
			
			if(LString.class.equals(type)){
				val=new LString(val);
			}
			this.stringMap.put(stringId, new Data(type, val, stringSource));

			codeSource = codeSource.replaceFirst(reg, stringId);
			stringSource = StringUtil.getFirstMatch(reg, codeSource);
			num++;
		}
		
		return codeSource;
	}
	
	

	@SuppressWarnings("rawtypes")
	private String extractNumber(String codeSource) {
		String numberSource;
		int num = this.numberMap.size();

		numberSource = StringUtil.getFirstMatch(RegexConstants.extractNumber, codeSource);
		while (numberSource != null) {
			String numberId = num + Constants.numberIdSuffix;

			String suffix = numberSource.substring(numberSource.length() - 1);
			String content = numberSource.substring(0, numberSource.length() - 1);
			Data data = new Data();
			data.setSource(numberSource);
			if (StringUtil.isNotEmpty(suffix) && suffix.matches(RegexConstants.numberSuffix)) {
				Object parser = Constants.numberParses.get(suffix);
				try {
					data.setValue(((Constructor) parser).newInstance(content));
				} catch (Throwable t) {
					Util.systemError(this,numberSource+":"+t.toString());
				}
				data.setType(Constants.numberTypes.get(suffix));
			} else {
				data.setValue(numberSource);
				data.setType(String.class);
			}

			this.numberMap.put(numberId, data);

			codeSource = codeSource.replaceFirst(RegexConstants.extractNumber, numberId);
			numberSource = StringUtil.getFirstMatch(RegexConstants.extractNumber, codeSource);
			num++;
		}
		return codeSource;
	}

	@Override
	public String toString() {
		return StringUtil.join(Constants.empty,"Code [idName=", idName, "]");
	}

	public String see() {
		String seeSource = this.source;
		String temp;
		String innerId = StringUtil.getFirstMatch(RegexConstants.extractFormId, seeSource);

		while (innerId != null) {
			temp = this.getFormMap().get(innerId).see();
			temp = java.util.regex.Matcher.quoteReplacement(temp);
			seeSource = seeSource.replaceFirst(RegexConstants.extractFormId, temp);
			innerId = StringUtil.getFirstMatch(RegexConstants.extractFormId, seeSource);
		}

		seeSource = Util.seeNumber(this, seeSource);
		seeSource = Util.seeString(this, seeSource);
		return seeSource;
	}

}
