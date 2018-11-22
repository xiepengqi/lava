package lava.core.keyword;

import lava.constant.Constants;
import lava.constant.MsgConstants;
import lava.core.Data;
import lava.core.Form;
import lava.util.StringUtil;
import lava.util.Util;

public class AssignForm extends Form {
	@Override
	public void parse() {
		super.parse();
	}

	@Override
	public void check() {
		super.check();
		if(this.args.size()<2){
			Util.syntaxError(this, MsgConstants.wrong_args_num);
		}

		for(String arg:this.args.subList(0,this.args.size()-1)){
			if (arg.startsWith(Constants.systemVarPrefix)) {
				Util.syntaxError(this,arg+":"+MsgConstants.no_assign);
			}
			if(!StringUtil.isDefVarAble(arg)){
				Util.syntaxError(this,arg+":"+MsgConstants.wrong_arg_name);
			}
		}
	}

	@Override
	public void run() throws Exception {
		super.run();
		processFormAssign();
	}

	private void processFormAssign() {
		Data value = this.parseFormArg(this.args.get(this.args.size() - 1));

		for (String arg : this.args.subList(0, this.args.size() - 1)) {
			Data data=this.parseFormArg(arg);
			data.setValue(value.getValue());
			data.setType(value.getType());
		}

		this.value = value.getValue();
		this.type = value.getType();
	}
}
