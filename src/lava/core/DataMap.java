package lava.core;

import java.util.HashMap;
import java.util.Map;

import lava.constant.Constants;
import lava.util.StringUtil;

public class DataMap {
	private Map<String, Data>	map	= new HashMap<String, Data>();

	public DataMap(){
		
	}
	
	public DataMap(Map map){
		this.putAll(map);
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

	public void putAll(Map map) {
		String keyStr=null;
		for(Object key:map.keySet()){
			keyStr=StringUtil.toString(key);
			if(StringUtil.isNotBlank(keyStr)){
				this.put(keyStr, map.get(key));
			}
		}
	}
}
