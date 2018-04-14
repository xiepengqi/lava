package lava.core;

import lava.Main;
import lava.constant.Constants;
import lava.constant.RegexConstants;
import lava.core.DataMap.DataInfo;
import lava.util.FileUtil;
import lava.util.StringUtil;
import lava.util.Util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Code {
	private String					filePath;
	private String					idName;

	private List<Form>				formSeq		= new ArrayList<Form>();

	private DataMap					dataMap		= new DataMap();

	private Map<String, Form>		formMap		= new HashMap<String, Form>();
	private Map<String, DataInfo>	stringMap	= new HashMap<String, DataInfo>();
	private Map<String, DataInfo>	numberMap	= new HashMap<String, DataInfo>();

	private boolean					isParsed;
	private boolean					isChecked;
	private boolean					isRuned;
	private boolean					debug;
	private String					source		= Constants.empty;

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Map<String, DataInfo> getNumberMap() {
		return numberMap;
	}

	public Map<String, DataInfo> getStringMap() {
		return stringMap;
	}

	public Map<String, Form> getFormMap() {
		return formMap;
	}

	public Code(String idName, String filePath) {
		this.filePath = filePath;
		this.idName = idName;

		for (String key : Main.config.keySet()) {
			dataMap.put(Constants.systemVarPrefix + key, Main.config.get(key));
		}

		dataMap.put("$ARGS", Main.ARGS);
		dataMap.put("$config", Main.config);
		dataMap.put("$codes", Main.codes);
		dataMap.put("$codeId", this.getIdName());

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
		} catch (Exception e) {
			Util.runtimeError(this,e.toString());
		}

		codeSource = extractString(codeSource);
		codeSource = extractNumber(codeSource);
		codeSource = extractForm(codeSource);

		this.source = codeSource;

		if (Main.syntaxError && !Main.repl) {
			System.exit(1);
		}
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
		if (Main.syntaxError && !Main.repl) {
			System.exit(1);
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
		codeSource=extractString(codeSource, RegexConstants.extractString, String.class);
		codeSource=extractString(codeSource, RegexConstants.extractLString, LString.class);
		
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
			this.stringMap.put(stringId, new DataInfo(type, val, stringSource));

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
			DataInfo data = new DataInfo();
			data.setSource(numberSource);
			if (StringUtil.isNotEmpty(suffix) && suffix.matches(RegexConstants.numberSuffix)) {
				Object parser = Constants.numberParses.get(suffix);
				try {
					data.setValue(((Constructor) parser).newInstance(content));
				} catch (Exception e) {
					Util.runtimeError(this,numberSource+":"+e.toString());
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
		return "Code [idName=" + idName + "]";
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
