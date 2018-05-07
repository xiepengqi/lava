package lava.core.keyword;

import java.util.List;
import java.util.Map;

import lava.Main;
import lava.core.Code;
import lava.core.Data;
import lava.core.Form;

public class EvalForm extends Form {
	@Override
	public void parse() {
		super.parse();
	}

	@Override
	public void check() {
		super.check();

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void run() throws Exception {
		super.run();

		List<Data> parseArgs = this.parseFormArgs(this.args);
		List codeIds = null;
		String cmd = null;

		Map<String,Object> result = null;

		for (Data data : parseArgs) {
			if (data.getValue() instanceof List) {
				codeIds = (List) data.getValue();
			} else {
				cmd = (String) data.getValue();
			}

			if (cmd == null) {
				continue;
			}

			if (codeIds == null) {
				result = this.inCode.eval(cmd);
				continue;
			}
			for (Object codeId : codeIds) {
				Code code = Main.codes.get(codeId);
				result = code.eval(cmd);
			}
		}
		this.value = result.get("value");
		this.type =(Class)result.get("type");
	}

}
