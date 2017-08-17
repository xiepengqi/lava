package lava.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lava.constant.Constants;
import lava.constant.RegexConstants;
import lava.core.DataMap.DataInfo;
import lava.core.keyword.FnForm;
import lava.core.keyword.JavaForm;
import lava.core.keyword.ListForm;
import lava.core.keyword.MapForm;
import lava.core.keyword.SubForm;
import lava.util.StringUtil;
import lava.util.Util;

public class Form {
	protected Code			inCode;
	protected List<Sub>		inSubSeq	= new ArrayList<Sub>();

	private Form			runBy		= null;

	private String			formId;
	protected String		source;
	protected Object		value;
	@SuppressWarnings("rawtypes")
	protected Class			type		= void.class;
	protected String		fnName;
	protected Sub			asSub;
	protected List<String>	args		= new ArrayList<String>();
	protected List<String>	elems		= new ArrayList<String>();

	private boolean			debug;

	public String getWhere() {
		if (this.inSubSeq.size() == 0) {
			return this.inCode.getIdName();
		}
		return this.inSubSeq.get(0).getIdName();
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Form getRunBy() {
		return runBy;
	}

	public void setRunBy(Form runBy) {
		this.runBy = runBy;
	}

	public List<String> getElems() {
		return elems;
	}

	public void setElems(List<String> elems) {
		this.elems = elems;
	}

	public List<Sub> getInSubSeq() {
		return inSubSeq;
	}

	public String getFnName() {
		return fnName;
	}

	public void setFnName(String fnName) {
		this.fnName = fnName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@SuppressWarnings("rawtypes")
	public Class getType() {
		return type;
	}

	@SuppressWarnings("rawtypes")
	public void setType(Class type) {
		this.type = type;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public Code getInCode() {
		return inCode;
	}

	public void setInCode(Code inCode) {
		this.inCode = inCode;
	}

	public void parse() throws Exception {

	}

	public void check() {

	}

	public void run() throws Exception {
		Util.debug(this, this.formId + ":" + this.source);
	}

	protected List<DataInfo> parseFormArgs(List<String> oArgs) throws Exception {
		List<DataInfo> parseArgs = new ArrayList<DataMap.DataInfo>();
		for (String arg : oArgs) {
			parseArgs.add(parseFormArg(arg));
		}
		return parseArgs;
	}

	protected DataInfo parseFormArg(String arg) throws Exception {
		DataMap.DataInfo data = null;

		data = this.inCode.getStringMap().get(arg);
		if (null != data) {
			return data;
		}

		data = this.inCode.getNumberMap().get(arg);
		if (null != data) {
			return data;
		}

		Form form = this.inCode.getFormMap().get(arg);
		if (null != form) {
			return new DataMap.DataInfo(form.getType(), form.getValue(), form.source, this.inCode.getFormMap());
		}

		for (Sub sub : this.inSubSeq) {
			data = sub.getDataMap().get(arg);
			if (null != data) {
				data.setFundIn(sub);
				if (data.getSource() == null) {
					data.setSource(arg);
				}
				return data;
			}
			data = sub.getClosure().get(arg);
			if (null != data) {
				data.setFundIn(sub);
				if (data.getSource() == null) {
					data.setSource(arg);
				}
				return data;
			}
		}

		data = this.inCode.getDataMap().get(arg);
		if (null != data) {
			data.setFundIn(this.inCode);
			if (data.getSource() == null) {
				data.setSource(arg);
			}
			return data;
		}

		return new DataMap.DataInfo(String.class, arg, arg, null);
	}

	@SuppressWarnings("rawtypes")
	public static Form createdForm(String source) throws InstantiationException, IllegalAccessException {
		Form form = null;
		List<String> elems = null;

		if (source.startsWith("{")) {
			form = new MapForm();
		} else if (source.startsWith("[")) {
			form = new ListForm();
		}

		elems = parseForm(source);

		if (null != form) {
			form.setArgs(elems);
			form.setElems(elems);
			return form;
		}

		if (elems.size() == 0) {
			return new Form();
		}
		Class formClass = Constants.keywords.get(elems.get(0));
		if (null != formClass) {
			form = (Form) formClass.newInstance();
		} else if (elems.get(0).contains(Constants.javaChar) && !elems.get(0).contains(Constants.subPrefix)) {
			form = new JavaForm();
		} else if (elems.get(0).startsWith(Constants.subPrefix)) {
			form = new SubForm();
		} else {
			form = new FnForm();
		}

		form.setElems(elems);
		form.setFnName(elems.get(0));
		form.setArgs(elems.subList(1, elems.size()));

		return form;
	}

	private static List<String> parseForm(String source) {
		List<String> list = new ArrayList<String>();
		source = source.replaceAll(RegexConstants.formBorder, "").trim();
		if (StringUtil.isEmpty(source)) {
			return list;
		}
		String[] elems = source.split("\\s+");
		list.addAll(Arrays.asList(elems));
		return list;
	}

	public void markRunBy(Form runByForm) {
		if (null != this.getRunBy()) {
			return;
		}
		this.setRunBy(runByForm);
		if (null != this.asSub) {
			return;
		}

		for (String arg : this.getElems()) {
			Form form = this.inCode.getFormMap().get(arg);
			if (null != form) {
				form.markRunBy(runByForm);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Form> getFormSeqWhichRunBy(Form runBy, List... objects) {
		List<Form> formSeq;
		if (objects.length == 0) {
			formSeq = new ArrayList<Form>();
		} else {
			formSeq = objects[0];
		}

		if (!runBy.equals(this.getRunBy())) {
			return formSeq;
		}
		for (String arg : this.getElems()) {
			Form form1 = this.getInCode().getFormMap().get(arg);

			if (null != form1) {
				form1.getFormSeqWhichRunBy(runBy, formSeq);
			}
		}
		formSeq.add(this);
		return formSeq;
	}

	public static void runFormSeq(List<Form> formSeq) throws Exception {
		runFormSeq(formSeq, null);
	}

	public static void runFormSeq(List<Form> formSeq, Action action) throws Exception {
		for (Form form : formSeq) {
			if (action != null)
				action.beforeRun(form);

			form.run();

			Util.debug(form, form.getFormId() + ":" + form.getType() + ":" + form.getValue());

			if (action != null)
				action.afterRun(form);
		}
	}

	public static class Action {
		public void beforeRun(Form form) {

		}

		public void afterRun(Form form) {

		}
	}

	public String see() {
		String seeSource = seeForm();
		seeSource = Util.seeNumber(this.inCode, seeSource);
		seeSource = Util.seeString(this.inCode, seeSource);

		return seeSource;
	}

	public String seeForm() {
		String seeSource = this.source;
		String temp = null;
		String innerId = StringUtil.getFirstMatch(RegexConstants.extractFormId, seeSource);

		while (innerId != null) {
			temp = this.inCode.getFormMap().get(innerId).seeForm();
			temp = java.util.regex.Matcher.quoteReplacement(temp);
			seeSource = seeSource.replaceFirst(RegexConstants.extractFormId, temp);
			innerId = StringUtil.getFirstMatch(RegexConstants.extractFormId, seeSource);
		}

		return seeSource;
	}

}
