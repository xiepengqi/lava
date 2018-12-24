package lava.core.keyword;

import java.util.*;

import lava.Main;
import lava.constant.Constants;
import lava.core.Code;
import lava.core.Data;
import lava.core.Form;
import lava.core.SysError;
import lava.util.StringUtil;
import lava.util.Util;

public class UseForm extends Form {

	private static Map<String, Integer>	useCase	= new HashMap<String, Integer>();

	static {
		useCase.put("String:", 1);
		useCase.put("String:String", 2);
		useCase.put("String:Collection", 3);
		useCase.put("String:Map", 4);
		useCase.put(":", 5);
		useCase.put("Map:", 1);
		useCase.put("Map:String", 2);
		useCase.put("Map:Collection", 3);
		useCase.put("Map:Map", 4);
	}

	@Override
	public void parse() {
		super.parse();

	}

	@Override
	public void check() {
		super.check();

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void run() throws Exception {
		super.run();
		this.value = new HashMap();
		this.type = Map.class;

		Code useCode;
		for (String arg : this.args) {
			Data data=this.parseFormArg(arg);
			Object value = data.getValue();
			if(!(value instanceof String)){
				continue;
			}
			useCode = Main.codes.get(data.getValue());
			if (null == useCode) {
				throw new SysError(this, String.valueOf(data.getValue()));
			}
			useCode.parse();
			useCode.check();
			useCode.run();
		}

		List<Data> parseArgs = parseFormArgs(this.args);
		int index = 0;
		boolean isLast;
		Map dataMap = null;
		while (true) {
			String key = genUseCaseKey(index, parseArgs);
			Integer catchCase = useCase.get(key);
			if (null == catchCase) {
				catchCase = 0;
			}
			if (catchCase == 5) {
				break;
			}

			Object value = parseArgs.get(index).getValue();
			if(value instanceof Map) {
				dataMap = (Map)value;
			}
			if (value instanceof String) {
				useCode = Main.codes.get(value);
				if (useCode !=null) {
					dataMap = useCode.getExports().getMap();
				}
			}
			switch (catchCase) {
				case 1:
					exportAll(dataMap);
					isLast = true;
					break;
				case 2:
					exportAll(dataMap);
					index += 1;
					isLast = false;
					break;
				case 3:
					Collection exportCollection = (Collection) parseArgs.get(index + 1).getValue();

					exportCollection(dataMap, exportCollection);
					index += 2;
					isLast = false;

					break;
				case 4:
					Map exportMap = (Map) parseArgs.get(index + 1).getValue();
					exportMap(dataMap, exportMap, this);
					index += 2;
					isLast = false;

					break;
				default:
					index += 1;
					isLast = false;

					break;
			}

			if (isLast) {
				break;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void exportMap(Map fromMap, final Map exportMap,final Form form) {
		Util.Action action = new Util.Action() {
			private boolean isOverAble = false;

			@Override
			public String defToKey(Object useKey) {
				String toKey = (String) exportMap.get(useKey);
				
				if (null == toKey) {
					toKey = (String) useKey;
				} else if(!StringUtil.isDataMapKeyAble(toKey)){
					throw new SysError(form, toKey);
				}

				if(exportMap.containsKey(useKey)){
					isOverAble = true;
				}

				return toKey;
			}

			@Override
			public boolean isOverAble() {
				boolean result = false;
				if(isOverAble){
					result = true;
					isOverAble = false;
				}
				return result;
			}

			@Override
			public Object defToValue(Object useValue) {
				if (useValue instanceof Data) {
					return new Data((Data) useValue);
				} else {
					return new Data(useValue);
				}
			}
		};

		export(fromMap, action);
	}

	@SuppressWarnings("rawtypes")
	private void exportCollection(Map fromMap, final Collection exportCollection) {
		Util.Action action = new Util.Action() {
			@SuppressWarnings("unchecked")
			@Override
			public Iterable<Object> defUseKeys() {
				return exportCollection;
			}
			@Override
			public Object defToValue(Object useValue) {
				if (useValue instanceof Data) {
					return new Data((Data) useValue);
				} else {
					return new Data(useValue);
				}
			}
		};

		export(fromMap, action);
	}

	private void exportAll(Map fromMap) {
		Util.Action action = new Util.Action() {
			@Override
			public boolean isOverAble() {
				return false;
			}
			@Override
			public Object defToValue(Object useValue) {
				if (useValue instanceof Data) {
					return new Data((Data) useValue);
				} else {
					return new Data(useValue);
				}
			}
		};
		export(fromMap, action);
	}

	@SuppressWarnings("rawtypes")
	private void export(Map fromMap, Util.Action action) {
		Map toMap;

		if (this.inSubSeq.size() > 0) {
			toMap = this.inSubSeq.get(0).getDataMap().getMap();
		} else {
			toMap = this.inCode.getDataMap().getMap();
		}

		for (Map.Entry entry : (Set<Map.Entry>)Util.putAll(fromMap, toMap, action).entrySet()) {
			((Map)this.value).put(entry.getKey(), entry.getValue());
		}
	}

	private String genUseCaseKey(int index, List<Data> parseArgs) {
		String first = Constants.empty;
		String sec = Constants.empty;

		if (index + 1 == parseArgs.size()) {
			first = getClassStr(parseArgs.get(index));
		}

		if (index + 1 < parseArgs.size()) {
			first = getClassStr(parseArgs.get(index));
			sec = getClassStr(parseArgs.get(index + 1));
		}
		return first + ":" + sec;
	}

	private String getClassStr(Data data) {
		if (null == data.getValue()) {
			return "null";
		}
		if (data.getValue().getClass().equals(String.class)) {
			return "String";
		}
		if (data.getValue() instanceof Collection) {
			return "Collection";
		}
		if (data.getValue() instanceof Map) {
			return "Map";
		}

		return data.getValue().getClass().toString();
	}

}
