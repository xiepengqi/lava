package lava.core.keyword;

import java.util.ArrayList;
import java.util.List;

import lava.core.DataMap.Data;
import lava.core.Form;

public class ListForm extends Form {
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

		List<Object> list = new ArrayList<Object>();
		List<Data> parseArgs = parseFormArgs(this.args);

		for (Data data : parseArgs) {
			list.add(data.getValue());
		}

		this.value = list;
		this.type = ArrayList.class;
	}
}
