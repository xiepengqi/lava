package lava.core.keyword;

import lava.constant.MsgConstants;
import lava.core.DataMap.Data;
import lava.core.Form;
import lava.util.StringUtil;
import lava.util.Util;

public class AndForm extends Form {
	@Override
	public void parse() {
		super.parse();

		Form form;
		for (String arg : this.args) {
			form = this.inCode.getFormMap().get(arg);
			if (null != form) {
				form.markRunBy(this);
			}
		}
	}

	@Override
	public void check() {
		super.check();

		if(this.args.size()<2){
			Util.syntaxError(this, MsgConstants.wrong_args_num);
		}

		for(String arg:this.args){
			if(StringUtil.isNumberId(this,arg)||StringUtil.isStringId(this,arg)){
				Util.syntaxError(this,arg+":"+MsgConstants.wrong_arg_type);
			}
		}
	}

	@Override
	public void run() throws Exception {
		super.run();

		Form form;
		boolean flag = false;
		for (String arg : this.args) {
			form = this.inCode.getFormMap().get(arg);
			if (null != form) {
				runFormSeq(form.getFormSeqWhichRunBy(this),null);
				flag = (Boolean) form.getValue();
			} else {
				Data data = parseFormArg(arg);
				flag = (Boolean) data.getValue();
			}
			if (!flag) {
				break;
			}
		}

		this.value = flag;
		this.type = Boolean.class;

	}

}
