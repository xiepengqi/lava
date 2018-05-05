package lava.core;

import java.util.ArrayList;
import java.util.List;

import lava.constant.Constants;
import lava.core.DataMap.Data;
import lava.util.StringUtil;

public class Sub {
	private DataMap				dataMap			= new DataMap();
	private List<Form>			formSeq			= new ArrayList<Form>();
	private String				name;
	private Code				inCode;
	private Form				asForm;
	private String				idName;
	private List<String> 		args=new ArrayList<String>();

	private List<Instance>	instancePool	= new ArrayList<Instance>();
	private DataMap				closure			= new DataMap();
	private boolean  isDebug=false;
	
	private Sub ing;

	public Sub(){

	}

	public Sub newSub() {
		Sub sub = new Sub();
		sub.dataMap = dataMap;
		sub.formSeq = formSeq;
		sub.name = name;
		sub.inCode = inCode;
		sub.asForm = asForm;
		sub.idName = idName;
		sub.args=args;
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

	public Sub getIng() {
		return ing;
	}

	public void setIng(Sub ing) {
		this.ing = ing;
	}

	public List<String> getArgs() {
		return args;
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
	public boolean isReturn(){
		return this.instancePool.get(this.instancePool.size() - 1).isReturn;
	}
	public boolean isDebug(){
		return this.isDebug;
	}
	public void setIsDebug(boolean isDeubg){
		this.isDebug=isDeubg;
	}
	public void run(DataMap dataMap) throws Exception {
		this.asForm.asSub.ing=this;
		
		Instance ins = new Instance(this);
		ins.dataMap.getMap().putAll(dataMap.getMap());
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
		private Class	type		= void.class;

		private boolean	isReturn	= false;

		private Instance(Sub sub) {
			this.dataMap.getMap().putAll(sub.dataMap.getMap());
			this.sub = sub;
		}

		public void run() throws Exception {
			final Instance self=this;

			Form.Action action=new Form.Action(){
				@Override
				public boolean beforeRun(Form form) {
					if (null != form.getRunBy()) {
						return false;
					}
					
					return true;
				}

				@Override
				public boolean afterRun(Form form) {
					if (form.asSub != null) {
						Sub superSub = form.inSubSeq.get(0);
						form.value = form.asSub.newSub();
						((Sub) form.value).closure.getMap().putAll(superSub.closure.getMap());
						((Sub) form.value).closure.getMap().putAll(superSub.getDataMap().getMap());
					}

					if (self.isReturn) {
						self.value = form.getValue();
						self.type = form.getType();
					}
					return true;
				}
			};

			Form.runFormSeq(this.sub.formSeq,action);

			int argsSize = this.sub.asForm.getArgs().size();
			if (argsSize > 0) {
				Data data = getSubReturnData(argsSize);

				this.value = data.getValue();
				this.type = data.getType();
			} else {
				this.value = null;
				this.type = void.class;
			}
		}

		private Data getSubReturnData(int argsSize) {
			List<String> argsList = this.sub.asForm.getArgs().subList(argsSize - 1, argsSize);
			String args = argsList.get(0);

			Data data = this.dataMap.get(args);
			if (null == data) {
				List<Data> parseArgs = this.sub.asForm.parseFormArgs(argsList);
				data = parseArgs.get(0);
			}

			return data;
		}

	}

	@Override
	public String toString() {
		return StringUtil.join(Constants.empty,"Sub [idName=",getIdName(),"]");
	}

	public String see() {
		return this.asForm.see();
	}

}
