package lava.core.keyword;

import lava.constant.Constants;
import lava.core.Form;
import lava.core.Sub;
import lava.util.StringUtil;

import java.util.Arrays;

public class SubForm extends Form {

	@Override
	public void parse() {
		super.parse();

		Sub sub = new Sub();

		String[] nameArgs=this.fnName.split(Constants.sep,2);
		this.fnName= nameArgs[0];

		if(nameArgs.length > 1){
			sub.getArgs().addAll(Arrays.asList(nameArgs[1].split(Constants.sep)));
		}

		sub.setName(this.fnName.replaceFirst(Constants.subPrefix, Constants.empty));
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
