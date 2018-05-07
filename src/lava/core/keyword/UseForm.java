package lava.core.keyword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		useCase.put("String:List", 3);
		useCase.put("String:Map", 4);
		useCase.put(":", 5);
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
		List<Code> useCodes=new ArrayList<Code>();
		Code useCode;
		for (String arg : this.args) {
			Data data=this.parseFormArg(arg);
			useCode = Main.codes.get(data.getValue());
			if (null == useCode) {
				throw new SysError(this, String.valueOf(data.getValue()));
			}
			useCodes.add(useCode);
			useCode.parse();
			useCode.check();
			useCode.run();
		}

		if(useCodes.size() == 0){
			return;
		}
		List<Data> parseArgs = parseFormArgs(this.args);
		int index = 0;
		boolean isLast;
		while (true) {
			String key = genUseCaseKey(index, parseArgs);
			Integer catchCase = useCase.get(key);
			if (null == catchCase) {
				catchCase = 0;
			}
			switch (catchCase) {
				case 1:
					useCode = Main.codes.get(parseArgs.get(index).getValue());
					useCode.run();

					exportAll(useCode);
					isLast = true;
					break;
				case 2:
					useCode = Main.codes.get(parseArgs.get(index).getValue());
					useCode.run();

					exportAll(useCode);
					index += 1;
					isLast = false;
					break;
				case 3:
					useCode = Main.codes.get(parseArgs.get(index).getValue());
					useCode.run();
					List exportList = (List) parseArgs.get(index + 1).getValue();

					exportList(useCode, exportList);
					index += 2;
					isLast = false;

					break;
				case 4:
					useCode = Main.codes.get(parseArgs.get(index).getValue());
					useCode.run();

					Map exportMap = (Map) parseArgs.get(index + 1).getValue();
					exportMap(useCode, exportMap, this);
					index += 2;
					isLast = false;

					break;
				case 5:
					isLast = true;

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
		
		Code lastCode=useCodes.get(useCodes.size()-1);
		
		this.value=lastCode.getValue();
		this.type=lastCode.getType();
	}

	@SuppressWarnings("rawtypes")
	private void exportMap(Code useCode, final Map exportMap,final Form form) {
		Util.Action action = new Util.Action() {
			@Override
			public String defToKey(Object useKey) {
				String key = (String) exportMap.get(useKey);
				
				if (null == key) {
					key = (String) useKey;
				} else if(!StringUtil.isDataMapKeyAble(key)){
					throw new SysError(form, key);
				}
				
				return key;
			}
		};

		export(useCode, action);
	}

	@SuppressWarnings("rawtypes")
	private void exportList(Code useCode, final List exportList) {
		Util.Action action = new Util.Action() {
			@SuppressWarnings("unchecked")
			@Override
			public Iterable<Object> defUseKeys() {
				return exportList;
			}

		};

		export(useCode, action);
	}

	private void exportAll(Code useCode) {
		Util.Action action = new Util.Action() {
			@Override
			public boolean isOverAble() {
				return false;
			}
		};
		export(useCode, action);
	}

	@SuppressWarnings("rawtypes")
	private void export(Code useCode, Util.Action action) {
		Map toMap;

		if (this.inSubSeq.size() > 0) {
			toMap = this.inSubSeq.get(0).getDataMap().getMap();
		} else {
			toMap = this.inCode.getDataMap().getMap();
		}

		Util.putAll(useCode.getExports().getMap(), toMap, action);
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
		if (data.getValue() instanceof List) {
			return "List";
		}
		if (data.getValue() instanceof Map) {
			return "Map";
		}

		return data.getValue().getClass().toString();
	}

}
