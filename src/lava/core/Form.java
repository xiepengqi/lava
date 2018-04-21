package lava.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lava.Main;
import lava.constant.Constants;
import lava.constant.RegexConstants;
import lava.core.DataMap.DataInfo;
import lava.core.keyword.FnForm;
import lava.core.keyword.JavaForm;
import lava.core.keyword.KeyForm;
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

	public void parse() {

	}

	public void check() {

	}

	public void run() throws Exception {
		if(this.getInSubSeq().size()>0){
			this.inSubSeq.set(0, this.inSubSeq.get(0).getAsForm().asSub.getIng());
			if(this.getInSubSeq().get(0).isReturn()){
				return;
			}
		}
		Util.debug(this, Util.debug_when_form_begin);
	}

	protected List<DataInfo> parseFormArgs(List<String> oArgs) {
		List<DataInfo> parseArgs = new ArrayList<DataMap.DataInfo>();
		for (String arg : oArgs) {
			parseArgs.add(parseFormArg(arg));
		}
		return parseArgs;
	}

	protected DataInfo parseFormArg(String arg){
		DataMap.DataInfo data;

		data = this.inCode.getStringMap().get(arg);
		if (null != data) {
			if(LString.class.equals(data.getType())){
				data=new DataInfo(String.class,((LString)data.getValue()).parse(this),data.getSource());
			}
			
			return data;
		}

		data = this.inCode.getNumberMap().get(arg);
		if (null != data) {
			return data;
		}

		Form form = this.inCode.getFormMap().get(arg);
		if (null != form) {
			return new DataMap.DataInfo(form.getType(), form.getValue());
		}

		for (Sub sub : this.inSubSeq) {
			data = sub.getDataMap().get(arg);
			if (null != data) {
				return data;
			}
			data = sub.getClosure().get(arg);
			if (null != data) {
				return data;
			}
		}

		data = this.inCode.getDataMap().get(arg);
		if (null != data) {
			return data;
		}

		throw new SysError(Util.getErrorStr(this,arg));
	}

	protected void runSub(Sub sub,List<DataInfo> parseArgs,List<Object> values) throws Exception {
		List elems=new ArrayList();
		List args=new ArrayList();
		boolean isDataInfo=false;
		if(parseArgs!=null){
			isDataInfo=true;
			elems=parseArgs;
		}else if(values!=null){
			elems=values;
		}

		if(runSubLink(sub,elems)){
			return;
		}

		DataMap dataMap=new DataMap();
		List<Map> subArgs=new ArrayList<Map>();
		
		List<String> subArgsName=new LinkedList<String>();
		subArgsName.addAll(sub.getArgs());
		
		for (int i = 0; i < elems.size(); i++) {
			String argName=subArgsName.size() > 0 ? subArgsName.remove(0) : null;
			Map<String,Object> temp=new HashMap<String,Object>();
			if(StringUtil.isNotBlank(argName)){
				List<Object> list=new ArrayList<Object>();
				if(argName.startsWith(Constants.expand)){
					do{
						list.add(isDataInfo ? ((DataInfo)elems.get(i)).getValue():elems.get(i));
						i++;
					}while((elems.size()-i) > subArgsName.size());	
					temp.put("arg",list);
					temp.put("argName", argName.substring(1));
					temp.put("isDataInfo", false);
				}else{
					temp.put("arg",elems.get(i));
					temp.put("argName", argName);
					temp.put("isDataInfo", isDataInfo);
				}
				subArgs.add(temp);
			}else{
				temp.put("arg",elems.get(i));
				temp.put("isDataInfo", isDataInfo);
				subArgs.add(temp);
			}
		}
		
		int argIndex=0;
		int argSize=subArgs.size();
		for(Map elem:subArgs){
			Object arg=elem.get("arg");
			String argName=(String)elem.get("argName");
			
			if((Boolean)elem.get("isDataInfo")){
				args.add(((DataInfo)arg).getValue());
				dataMap.putData("$"+argIndex, (DataInfo)arg);
				dataMap.putData("$-"+(argSize - argIndex), (DataInfo)arg);
				if(argName != null){
					dataMap.putData(argName, (DataInfo)arg);
				}
			}else{
				args.add(arg);
				dataMap.put("$"+argIndex, arg);
				dataMap.put("$-"+(argSize - argIndex), arg);
				if(argName != null){
					dataMap.put(argName, arg);
				}
			}
			argIndex++;
		}

		dataMap.put("$args", args);
		sub.run(dataMap);
	}

	private boolean runSubLink(Sub sub,List elems) throws Exception {
		boolean use=false;

		Object key;

		Object subLink= Main.subLinks.remove(sub);
		Object value=subLink;
		if(subLink==null){
			subLink= Main.subLinks.remove(sub.getName());
			key=sub.getName();
			value=subLink;
		}else{
			key=sub;
		}

		if(subLink==null){
			return use;
		}

		if(!(subLink instanceof Sub)){
			subLink=getSubFromScope(StringUtil.toString(subLink));
		}

		if(subLink ==null){
			Main.subLinks.put(key,value);
			return use;
		}

		use=true;


		List temp=new ArrayList();

		if(elems.size()==0){
			temp.add(sub);
			runSub((Sub)subLink,null,temp);
		}else if(elems.get(0) instanceof DataInfo){
			temp.add(new DataInfo(Sub.class,sub));
			temp.addAll(elems);
			runSub((Sub)subLink,temp,null);
		}else{
			temp.add(sub);
			temp.addAll(elems);
			runSub((Sub)subLink,null,temp);
		}

		Main.subLinks.put(key,value);
		return use;
	}

	protected Sub getSubFromScope(String fnName) {
		if (fnName.equals(this.getFnName())&&StringUtil.isFormId(this, this.getFnName())) {
			Form form=this.getInCode().getFormMap().get(this.getFnName());
			Object obj = form.getValue();
			if (obj instanceof Sub) {
				return (Sub)obj;
			}
		}

		Sub findSub;
		for (Sub sub : this.getInSubSeq()) {
			findSub = findSub(sub.getDataMap(), fnName);
			if (null != findSub) {
				return findSub;
			}
			findSub = findSub(sub.getClosure(), fnName);
			if (null != findSub) {
				return findSub;
			}
		}

		findSub = findSub(this.getInCode().getDataMap(), fnName);
		if (null != findSub) {
			return findSub;
		}
		return null;
	}

	private Sub findSub(DataMap dataMap, String fnName) {
		DataInfo data = dataMap.get(Constants.subPrefix + fnName);
		if (null == data) {
			data = dataMap.get(fnName);
		}

		if (data != null && data.getValue() instanceof Sub) {
			return (Sub)data.getValue();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static Form createdForm(String source) {
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
			try {
				form = (Form) formClass.newInstance();
			} catch (Exception e) {
				Util.runtimeError(source+":"+e.toString());
			}
		} else if (elems.get(0).contains(Constants.javaChar)) {
			form = new JavaForm();
		} else if (elems.get(0).startsWith(Constants.subPrefix)) {
			form = new SubForm();
		} else if(elems.get(0).startsWith(Constants.sepOrObjChar)){ 
			form = new KeyForm();
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

	public static void runFormSeq(List<Form> formSeq, Action action) throws Exception {
		List<Form> safeFormSeq = new ArrayList<Form>();
		if(!Main.syntaxError){
			safeFormSeq.addAll(formSeq);
		}
		for (Form form : safeFormSeq) {
			if (action != null){
				if(!action.beforeRun(form)){
					continue;
				}
			}

			try{
				form.run();
				Util.debug(form, Util.debug_when_form_end);
			}catch(SysError e){
				throw new SysError(e.getMessage());
			}catch (Exception e){
				e.printStackTrace();
				throw new SysError(Util.getErrorStr(form,e.toString()));
			}

			if (action != null){
				if(!action.afterRun(form)){
					continue;
				}
			}
		}
	}

	public interface Action {
		boolean beforeRun(Form form);
		boolean afterRun(Form form);
	}

	public String see() {
		String seeSource = seeForm();
		seeSource = Util.seeNumber(this.inCode, seeSource);
		seeSource = Util.seeString(this.inCode, seeSource);

		return seeSource;
	}

	private String seeForm() {
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
