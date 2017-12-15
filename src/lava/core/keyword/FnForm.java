package lava.core.keyword;

import java.util.ArrayList;
import java.util.List;

import lava.Main;
import lava.constant.Constants;
import lava.core.DataMap;
import lava.core.DataMap.DataInfo;
import lava.core.Form;
import lava.core.Sub;
import lava.util.StringUtil;
import lava.util.Util;

public class FnForm extends Form {
	@Override
	public void parse() throws Exception {
		super.parse();
	}

	@Override
	public void check() {
		super.check();

	}

	@Override
	public void run() throws Exception {
		super.run();
		List<DataInfo> parseArgs = this.parseFormArgs(this.args);
		DataInfo subData = getSubFromScope(this,this.fnName);

		if(subData.getValue()==null){
			Util.runtimeError(this,this.fnName);
		}

		runSub((Sub)subData.getValue(),parseArgs,null,this);
		this.type=((Sub)subData.getValue()).getAsForm().getType();
		this.value=((Sub)subData.getValue()).getAsForm().getValue();
	}

}
