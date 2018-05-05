package lava.core.keyword;

import lava.core.DataMap.Data;
import lava.core.Form;
import lava.core.Sub;

public class ReturnForm extends Form {
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

		if(this.args.size()>0) {
			Data data = this.parseFormArg(this.args.get(this.args.size() - 1));
			this.type = data.getType();
			this.value = data.getValue();
		}
		if(this.inSubSeq.size() > 0){
			this.inSubSeq.get(0).setIsReturn(true);

			Sub sub=this.inSubSeq.get(0);
			sub.getAsForm().setType(this.type);
			sub.getAsForm().setValue(this.value);
		}else{
			this.inCode.setReturn(true);
			this.inCode.setType(this.type);
			this.inCode.setValue(this.value);
		}
		
	}
}
