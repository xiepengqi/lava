package lava.core.keyword;

import java.util.ArrayList;
import java.util.List;

import lava.Main;
import lava.core.Data;
import lava.core.Form;
import lava.util.StringUtil;

public class LoadForm extends Form {
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
		
		List<String> paths = new ArrayList<String>();
		for (Data data : this.parseFormArgs(this.args)) {
			paths.add(StringUtil.toString(data.getValue()));
		}

		Main.initSource(paths);
	}
}
