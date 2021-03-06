package lava.core.keyword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lava.core.Data;
import lava.core.Form;
import lava.core.SysError;
import lava.core.Sub;
import lava.util.Util;

public class ForeachForm extends Form {
	@Override
	public void parse() {

	}

	@Override
	public void check() {
		super.check();
	}

	@Override
	public void run() throws Exception {
		super.run();
		processFormForeach();
	}

	@SuppressWarnings("rawtypes")
	private void processFormForeach() throws Exception {
		List<Data> parseArgs = this.parseFormArgs(this.args);

		if (parseArgs.get(parseArgs.size() - 1).getValue() instanceof Sub) {
			Sub sub = (Sub) parseArgs.get(parseArgs.size() - 1).getValue();

			for (int i = 0; i < parseArgs.size() - 1; i++) {
				Object elems=parseArgs.get(i).getValue();
				if(elems instanceof Map){
					elems=((Map)elems).entrySet();
				}
				if (elems instanceof Object[]){
					elems = Arrays.asList((Object[])elems);
				}
				for (Object j : (Iterable) elems) {
					List<Object> values = new ArrayList<Object>();
					values.add(j);

					runSub(sub,null,values);

					if (!Util.isValid(sub.getAsForm().getValue())) {
						break;
					}
				}
			}

			this.value = null;
			this.type = Object.class;
		} else {
			throw new SysError(this, this.args.get(0));
		}
	}
}
