package lava.core.keyword;

import lava.constant.MsgConstants;
import lava.core.Data;
import lava.core.Form;
import lava.core.SysError;
import lava.util.Util;

public class ThrowForm extends Form {
	@Override
	public void parse() {
		super.parse();

	}

	@Override
	public void check() {
		super.check();
		if(this.args.size()<1){
			Util.syntaxError(this, MsgConstants.wrong_args_num);
		}
	}

	@Override
	public void run() throws Exception {
		super.run();
		Data data=parseFormArg(this.args.get(0));
		throw new SysError(this, String.valueOf(data.getValue()));
	}
}
