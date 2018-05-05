package lava.core.keyword;

import lava.core.DataMap;
import lava.core.DataMap.Data;
import lava.core.Form;
import lava.core.SysError;
import lava.util.StringUtil;
import lava.util.Util;

public class DefForm extends Form {
	@Override
	public void parse() {

	}

	@Override
	public void check() {
		super.check();

		for(String arg:this.args.subList(0,this.args.size()-1)){
			if(!StringUtil.isVarAble(arg)){
				Util.syntaxError(this,arg);
			}
		}
	}

	@Override
	public void run() throws Exception {
		super.run();

		DataMap dataMap = getDataMap();

		if (this.args.size() < 2) {
			dealOneArgs(dataMap);
		}else{
			dealMoreArgs(dataMap);
		}

	}

	private void dealMoreArgs(DataMap dataMap) {
		String arg = this.args.get(this.args.size() - 1);
		Data value = parseFormArg(arg);

		for (String var : this.args.subList(0, this.args.size() - 1)) {
            if (dataMap.getMap().containsKey(var)) {
                throw new SysError(this, var);
            } else {
                dataMap.putData(var, new Data(value.getType(),value.getValue(),value.getSource()));
            }
        }

		this.value = value.getValue();
		this.type = value.getType();
	}

	private void dealOneArgs(DataMap dataMap) {
		if (dataMap.getMap().containsKey(this.args.get(0))) {
            throw new SysError(this,parseFormArg(this.args.get(0)).getSource());
        }

		dataMap.putData(this.args.get(0), new Data(void.class,null));

		this.value = null;
		this.type = void.class;
	}

	private DataMap getDataMap() {
		DataMap dataMap;
		if (inSubSeq.size() > 0) {
			dataMap = this.inSubSeq.get(0).getDataMap();
		} else {
			dataMap = this.inCode.getDataMap();
		}
		return dataMap;
	}


}
