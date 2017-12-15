package lava.core;

import java.util.HashMap;
import java.util.Map;

public class DataMap {
	private Map<String, DataInfo>	map	= new HashMap<String, DataInfo>();

	public static class DataInfo {
		public DataInfo() {
		}

		@SuppressWarnings("rawtypes")
		public DataInfo(Class type, Object value, String source, Object fundIn) {
			this.type = type;
			this.value = value;
			this.source = source;
			this.fundIn = fundIn;
		}

		private String	source;
		private Object	value;
		@SuppressWarnings("rawtypes")
		private Class	type	= void.class;
		private Object	fundIn	= null;

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public Object getFundIn() {
			return fundIn;
		}

		public void setFundIn(Object fundIn) {
			this.fundIn = fundIn;
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

	}

	public void put(String key, Object value) {
		DataInfo data = this.map.get(key);
		if (null == data) {
			data = new DataInfo();
		}
		data.setType(null == value ? void.class : value.getClass());
		data.setValue(value);
		this.map.put(key, data);
	}

	public DataInfo get(String key) {
		return this.map.get(key);
	}

	public void putData(String key, DataInfo data) {
		this.map.put(key, data);
	}

	public Object getValue(String key) {
		if (null == this.map.get(key)) {
			return null;
		}
		return this.map.get(key).getValue();
	}

	@SuppressWarnings("rawtypes")
	public Class getType(String key) {
		if (null == this.map.get(key)) {
			return void.class;
		}
		return this.map.get(key).getType();
	}

	public Map<String, DataInfo> getMap() {
		return map;
	}

}
