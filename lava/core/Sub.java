package lava.core;

import java.util.ArrayList;
import java.util.List;

import lava.constant.Constants;
import lava.core.DataMap.DataInfo;
import lava.util.Util;

public class Sub {
	private DataMap				dataMap			= new DataMap();
	private List<Form>			formSeq			= new ArrayList<Form>();
	private String				name;
	private Code				inCode;
	private Form				asForm;
	private String				idName;
	private List<Sub.Instance>	instancePool	= new ArrayList<Sub.Instance>();
	private DataMap				closure			= new DataMap();

	public Sub newSub() {
		Sub sub = new Sub();
		sub.dataMap = dataMap;
		sub.formSeq = formSeq;
		sub.name = name;
		sub.inCode = inCode;
		sub.asForm = asForm;
		sub.idName = idName;
		return sub;
	}

	public String getIdName() {
		if (null != this.idName) {
			return this.idName;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = this.asForm.getInSubSeq().size() - 1; i >= 0; i--) {
			sb.append(this.asForm.getInSubSeq().get(i).getName());
			sb.append(Constants.subPrefix);
		}
		this.idName = this.inCode.getIdName() + Constants.subPrefix + sb.toString() + this.name;
		return this.idName;
	}

	public DataMap getClosure() {
		return closure;
	}

	public void setClosure(DataMap closure) {
		this.closure = closure;
	}

	public Form getAsForm() {
		return asForm;
	}

	public void setAsForm(Form asForm) {
		this.asForm = asForm;
	}

	public DataMap getDataMap() {
		if (this.instancePool.size() == 0) {
			return this.dataMap;
		}
		return this.instancePool.get(this.instancePool.size() - 1).dataMap;
	}

	public List<Form> getFormSeq() {
		return formSeq;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInCode(Code inCode) {
		this.inCode = inCode;
	}

	public void setIsReturn(boolean isReturn) {
		this.instancePool.get(this.instancePool.size() - 1).isReturn = isReturn;
	}

	public void run() throws Exception {
		Sub.Instance ins = new Sub.Instance(this);
		instancePool.add(ins);

		ins.run();

		this.asForm.setValue(ins.value);
		this.asForm.setType(ins.type);
		instancePool.remove(ins);

		if (instancePool.size() != 0) {
			return;
		}

		for (String key : new ArrayList<String>(this.dataMap.getMap().keySet())) {
			if (key.startsWith(Constants.subPrefix)) {
				continue;
			}
			this.dataMap.getMap().remove(key);
		}
	}

	class Instance {
		private Sub		sub;

		public DataMap	dataMap		= new DataMap();
		public Object	value		= null;
		@SuppressWarnings("rawtypes")
		public Class	type		= void.class;

		public boolean	isReturn	= false;

		private Instance(Sub sub) {
			this.dataMap.getMap().putAll(sub.dataMap.getMap());
			this.sub = sub;
		}

		public void run() throws Exception {
			for (Form form : this.sub.formSeq) {
				int index = form.inSubSeq.indexOf(this.sub.asForm.asSub);
				form.inSubSeq.set(index, this.sub);

				if (null != form.getRunBy()) {
					continue;
				}
				form.run();
				if (form.asSub != null) {
					Sub superSub = form.inSubSeq.get(0);
					form.value = form.asSub.newSub();
					((Sub) form.value).closure.getMap().putAll(superSub.closure.getMap());
					((Sub) form.value).closure.getMap().putAll(superSub.getDataMap().getMap());
				}
				form.inSubSeq.set(index, this.sub.asForm.asSub);
				Util.debug(form, form.getFormId() + ":" + form.getType() + ":" + form.getValue());

				if (this.isReturn) {
					this.value = form.getValue();
					this.type = form.getType();
					return;
				}
			}

			int argsSize = this.sub.asForm.getArgs().size();
			if (argsSize > 0) {
				DataInfo data = getSubReturnData(argsSize);

				this.value = data.getValue();
				this.type = data.getType();
			} else {
				this.value = null;
				this.type = void.class;
			}
		}

		private DataInfo getSubReturnData(int argsSize) throws Exception {
			List<String> argsList = this.sub.asForm.getArgs().subList(argsSize - 1, argsSize);
			String args = argsList.get(0);

			DataInfo data = this.dataMap.get(args);
			if (null == data) {
				List<DataInfo> parseArgs = this.sub.asForm.parseFormArgs(argsList);
				data = parseArgs.get(0);
			}

			return data;
		}

	}

	@Override
	public String toString() {
		return "Sub [idName=" + getIdName() + "]";
	}

	public String see() {
		return this.asForm.see();
	}

}
