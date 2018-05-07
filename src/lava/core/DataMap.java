package lava.core;

import lava.constant.Constants;
import lava.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class DataMap {
	private Map<String, Data>	map	= new HashMap<String, Data>();

	public DataMap(){
		
	}

	public void put(String key, Object value) {
		Data data = this.map.get(key);
		if (null == data) {
			data = new Data();
		}
		data.setType(Data.getClass(value));
		data.setValue(value);
		this.map.put(key, data);
	}

	public Data get(String key) {
		return this.map.get(key);
	}

	public void putData(String key, Data data) {
		this.map.put(key, data);
	}

	public Map<String, Data> getMap() {
		return map;
	}

	@Override
	public String toString() {
		return StringUtil.join(Constants.empty,"DataMap [size=", map.size(), "]");
	}
}
