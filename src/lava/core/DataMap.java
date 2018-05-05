package lava.core;

import java.util.HashMap;
import java.util.Map;

public class DataMap {
	private Map<String, Data>	map	= new HashMap<String, Data>();

	public DataMap(){
		
	}
	
	public DataMap(Map<String,Data> map){
		
	}
	
	public static class Data {
		public Data() {
		}

		public Data(Data dataInfo) {
			this.type = dataInfo.getType();
			this.value = dataInfo.getValue();
			this.source = dataInfo.getSource();
		}
		@SuppressWarnings("rawtypes")
		public Data(Class type, Object value) {
			this.type = type;
			this.value = value;
		}
		@SuppressWarnings("rawtypes")
		public Data(Class type, Object value, String source) {
			this.type = type;
			this.value = value;
			this.source = source;
		} 

		private String	source;
		private Object	value;
		@SuppressWarnings("rawtypes")
		private Class	type	= void.class;
		
		public String toString(){
			return this.type+":"+this.value;
		}
		
		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		@SuppressWarnings("rawtypes")
		public Class getType() {
			return type;
		}

		@SuppressWarnings("rawtypes")
		public void setType(Class type) {
			this.type = type;
		}

		public static Class getClass(Object obj){
			return obj==null ? void.class:obj.getClass();
		}
		public static Class getType(Data data){
			return data==null ? void.class:data.getType();
		}
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

}
