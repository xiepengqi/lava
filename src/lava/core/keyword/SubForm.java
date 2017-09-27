package lava.core.keyword;

import lava.Main;
import lava.constant.Constants;
import lava.core.Form;
import lava.core.Sub;
import lava.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SubForm extends Form {

	@Override
	public void parse() throws Exception {
		super.parse();

		String[] subNames=this.fnName.split(Constants.subPrefix);
		List<String> subNameList=new ArrayList<String>();
		for(String subName:subNames){
			if(subName!=null&&subName.length()>0){
				subNameList.add(subName);
			}
		}
		String subName=Constants.empty;
		if(subNameList.size()>0){
			subName=subNameList.remove(subNameList.size()-1);
		}

		if(subNameList.size()>0){
			List<String> subLink=Main.subLinks.get(subName);
			if(subLink==null){
				Main.subLinks.put(subName,subNameList);
			}else{
				subLink.addAll(subNameList);
			}
		}

		this.fnName=Constants.subPrefix+subName;

		Sub sub = new Sub();
		sub.setName(subName);
		sub.setInCode(this.inCode);
		sub.setAsForm(this);
		this.asSub = sub;

		markScopeForForm(this, sub);

	}

	private void markScopeForForm(Form form, Sub func) {
		for (String arg : form.getElems()) {
			if (null != this.inCode.getFormMap().get(arg)) {
				this.inCode.getFormMap().get(arg).getInSubSeq().add(func);
				markScopeForForm(this.inCode.getFormMap().get(arg), func);
			}
		}
	}

	@Override
	public void check() {
		super.check();

		if (StringUtil.isEmpty(this.asSub.getName())) {
			return;
		}

		if (this.inSubSeq.size() > 0) {
			this.inSubSeq.get(0).getDataMap().put(this.fnName, this.asSub);
		} else {
			this.inCode.getDataMap().put(this.fnName, this.asSub);
		}
	}

	@Override
	public void run() throws Exception {
		super.run();

		this.type = Sub.class;
		this.value = this.asSub;
	}

}
