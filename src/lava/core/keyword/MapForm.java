package lava.core.keyword;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lava.core.DataMap.DataInfo;
import lava.core.Form;

public class MapForm extends Form {
	@Override
	public void parse() {
		super.parse();

	}

	@Override
	public void check() {
		super.check();

	}

	@Override
	public void run() throws Exception {
		super.run();

		Map<Object, Object> map = new HashMap<Object, Object>();
		List<DataInfo> parseArgs = parseFormArgs(this.args);

		for (int i = 0; i < parseArgs.size() - 1; i += 2) {
			map.put(parseArgs.get(i).getValue(), parseArgs.get(i + 1).getValue());
		}

		if (parseArgs.size() % 2 == 1) {
			map.put(parseArgs.get(parseArgs.size() - 1).getValue(), null);
		}

		this.value = map;
		this.type = HashMap.class;
	}
}
