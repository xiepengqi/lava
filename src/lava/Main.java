package lava;

import lava.constant.Constants;
import lava.core.Code;
import lava.core.SysError;
import lava.util.FileUtil;
import lava.util.JavaUtil;
import lava.util.Util;

import java.io.File;
import java.util.*;

public class Main {
	public static final Map<String, Code> codes = new HashMap<String, Code>();
	public static final Map<String, String> config = new HashMap<String, String>();
	public static boolean syntaxError = false;
	public static final List<String> ARGS = new ArrayList<String>();
	public static final Map<Object,Object> subLinks=new HashMap<Object,Object>();

	public static boolean debug=false;
	public static boolean repl = false;

	public static void main(String[] args) {
		List<String> codePaths = new ArrayList<String>();
		for (String arg : args) {
			if (arg.contains(Constants.configSplit)) {
				String[] kv = arg.split(Constants.configSplit, 2);
				config.put(kv[0], kv[1]);
			} else {
				if (codePaths.size() > 0) {
					ARGS.add(arg);
				} else {
					codePaths.add(arg);
				}
			}
		}

		Map<String, String> envs = System.getenv();
		List<String> initSourcePath = new ArrayList<String>();
		for (String key : envs.keySet()) {
			if (key.equalsIgnoreCase("lava_home")) {
				initSourcePath.add(envs.get(key) + "/lib");
			}
		}

		initSource(initSourcePath);

		if (codePaths.size() == 0) {
			startRepl();
			return;
		}

		List<Code> mainCodes = initSource(codePaths);

		for (Code code : mainCodes) {
			try {
				code.parse();
				code.check();
				code.run();
			}catch (SysError e) {
				Util.runtimeError(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				Util.runtimeError(code, e.toString());
			}
		}

	}

	public static void startRepl(){
		Main.repl = true;
		Code code = new Code("lava.repl", null);
		Main.codes.put(code.getIdName(), code);

		String line = "(/repl (if (def? $-1) $-1  ''))";
		try {
			code.eval(line);
		}catch (SysError e) {
			Util.runtimeError(e.getMessage());
		} catch (Exception e) {
			Util.runtimeError("lava.repl:"+e.toString());
		}

		Scanner scanner = new Scanner(System.in);
		while (line != null) {
			System.out.print(Constants.replPrefix);
			line = scanner.nextLine();
			String eof=null;
			while (line.endsWith(Constants.replPrefix)||(eof!=null&&!line.trim().endsWith(eof))) {
				if(eof==null){
					eof=line.substring(0,line.length()-1).trim();
					line=Constants.empty;
				}
				line += scanner.nextLine()+Constants.newLine;
			}
			if(eof!=null){
				line=line.substring(0,line.lastIndexOf(eof));
			}
			if (line.length() == 0) {
				continue;
			}
			try {
				System.out.println(code.eval("(repl " + line + " )").get("value"));
			}catch(SysError e){
				Util.runtimeError(e.getMessage());
			} catch (Exception e) {
				Util.runtimeError("lava.repl:"+e.toString());
			}
		}
	}

	public static List<Code> initSource(List<String> codePath) {
		List<String> paths = new ArrayList<String>();
		for (String path : codePath) {
			if (!path.contains(";")) {
				paths.add(path);
				continue;
			}
			String[] args = path.split(";");
			paths.addAll(Arrays.asList(args));
		}

		Util.unique(paths, new Util.Action() {
			@Override
			public boolean isRemoveAble(Object o) {
				String path = (String) o;
				if (path.trim().length() > 0) {
					return false;
				}
				return true;
			}

		});

		final List<Code> list = new ArrayList<Code>();
		FileUtil.Action action = new FileUtil.Action() {

			@Override
			public void action(File topFile, File file) {
				if (file.getName().endsWith(".jar")) {
					try {
						JavaUtil.loadjar(file);
					} catch (Exception e) {
						Util.runtimeError("fail to load jar file:"+file.getAbsolutePath());
					}
					return;
				}
				if (!file.getName().endsWith(".lava")) {
					return;
				}
				String idName;
				if (topFile.isDirectory()) {
					idName = getIdName(topFile.getPath(), file.getAbsolutePath());
				} else {
					idName = topFile.getName().replaceFirst("\\.lava$",
							Constants.empty);
				}

				if (codes.containsKey(idName)) {
					list.remove(codes.get(idName));
				}
				Code code = new Code(idName, file.getAbsolutePath());
				codes.put(idName, code);
				list.add(code);

			}

			private String getIdName(String homePath, String filePath) {
				return filePath.substring(homePath.length()+1).replaceAll("\\.[^/\\.]+$", "").replaceAll("[/\\\\]+",".");
			}

		};

		for (String path : paths) {
			FileUtil.traverseFolder(path, action);
		}
		return list;
	}

}
