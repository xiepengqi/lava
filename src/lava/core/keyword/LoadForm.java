package lava.core.keyword;

import java.util.ArrayList;
import java.util.List;

import lava.Main;
import lava.core.DataMap.DataInfo;
import lava.core.Form;

public class LoadForm extends Form {
	@Override
	public void parse() throws Exception {
		super.parse();

		List<String> paths = new ArrayList<String>();
		for (DataInfo data : this.parseFormArgs(this.args)) {
			paths.add((String) data.getValue());
		}

		Main.initSource(paths);
	}

	@Override
	public void check() {
		super.check();
	}

	@Override
	public void run() throws Exception {
		super.run();

	}
}
