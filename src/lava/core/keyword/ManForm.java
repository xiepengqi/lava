package lava.core.keyword;

import java.util.HashMap;
import java.util.Map;

import lava.core.Form;
import lava.core.Sub;
import lava.util.Util;


public class ManForm extends Form{
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
		
		Map<String, Object> map=new HashMap<String,Object>();
		
		
		Util.Action action=new Util.Action(){
			public boolean isOverAble() {
				return false;
			}
		};
		
		for (Sub sub : this.inSubSeq) {
			Util.putAll(sub.getDataMap().getMap(), map, action);
			Util.putAll(sub.getClosure().getMap(), map, action);	
		}
		
		Util.putAll(this.inCode.getDataMap().getMap(), map, action);
		this.type=map.getClass();
		this.value=map;
	}

}
