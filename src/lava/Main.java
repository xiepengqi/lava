package lava;

import lava.constant.Constants;
import lava.constant.MsgConstants;
import lava.core.Code;
import lava.core.JarLoader;
import lava.core.SysError;
import lava.util.FileUtil;
import lava.util.JavaUtil;
import lava.util.StringUtil;
import lava.util.Util;

import java.io.File;
import java.util.*;

public class Main {
	public static final Map<String, Code> codes = new HashMap<String, Code>();
	public static final Map<String, String> config = new HashMap<String, String>();
	public static boolean syntaxError = false;
	public static final List<String> ARGS = new ArrayList<String>();
	public static final Map<Object,Object> subLinks=new HashMap<Object,Object>();
	public static final Map<String, Class> jarClass = new HashMap<String, Class>();
	public static final List<String> urls = new ArrayList<String>();
	public static final List<String> jars = new ArrayList<String>();
	public static final Map<String, Code> modules = new HashMap<String, Code>();

	private static final List<Code> tempList = new ArrayList<Code>();

	public static boolean debug=false;
	public static boolean repl = false;

	private static final JarLoader jarLoader = new JarLoader();
	
	public static void main(String[] args) {
		List<String> codePaths = new ArrayList<String>();
		for (String arg : args) {
			if (arg.contains(Constants.configSplit)) {
				String[] kv = arg.split(Constants.configSplit, 2);
				if(kv.length < 2){
					continue;
				}
				String key=kv[0].trim();
				if(StringUtil.isBlank(key)){
					continue;
				}
				config.put(key, kv[1].trim());
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
		List<String> homePath = new ArrayList<String>();
		List<String> libPath = new ArrayList<String>();
		for (String key : envs.keySet()) {
			if (key.equalsIgnoreCase("lava_home")) {
				homePath.add(envs.get(key) + "/lib");
			}
			if (key.equalsIgnoreCase("lava_lib_")) {
				libPath.add(envs.get(key));
			}
		}
		initSourcePath.addAll(homePath);
		initSourcePath.addAll(libPath);

		initSource(initSourcePath);

		File file=new File(System.getProperty("user.dir"));
		for (File subFile : file.listFiles()) {
			if (subFile.isDirectory()) {
				continue;
			}
			action.action(file, subFile);
		}

		if (codePaths.size() == 0) {
			try {
				jarClass.putAll(JavaUtil.getJarLoaderClass(jarLoader));
				startRepl();
			} catch (SysError e) {
				Util.systemError(e.getMessage());
			} catch (Throwable t) {
				Util.systemError("lava.repl:"+t.toString());
			}
			return;
		}

		List<Code> mainCodes = initSource(codePaths);
		if (mainCodes.size() <1 && codePaths.size() > 0 && codes.containsKey(codePaths.get(0))) {
			mainCodes.add(codes.get(codePaths.get(0)));
		}
		if (mainCodes.size() < 1) {
			Util.systemError(MsgConstants.no_code_found);
		}
		for (Code code : mainCodes) {
			try {
				code.parse();
				code.check();
				code.run();
			}catch (SysError e) {
				Util.systemError(e.getMessage());
			} catch (Throwable t) {
				Util.systemError(code, t.toString());
			}
		}

	}

	private static void startRepl() throws Exception{
		Main.repl = true;
		Code code = new Code(null, null, "lava.repl");
		Main.codes.put(code.getIdName(), code);

		String line = "(/repl:code code)";
		try {
			code.eval(line);
		}catch (SysError e) {
			Util.systemError(e.getMessage());
		} catch (Throwable t) {
			Util.systemError("lava.repl:"+t.toString());
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
				Object evalResult=code.eval("(repl " + line + " )").get("value");
				System.out.println(StringUtil.toFmtString(evalResult));
			}catch(SysError e){
				Util.systemError(e.getMessage());
			} catch (Throwable t) {
				Util.systemError("lava.repl:"+t.toString());
			}
		}
	}
	private static FileUtil.Action action = new FileUtil.Action() {

		@Override
		public void action(File topFile, File file) {
			if (file.getName().endsWith(".jar")) {
				try {
					jarLoader.loadJar(file.toURI().toURL());;
				} catch (Throwable t) {
					Util.systemError("fail to load jar file:" + file.getAbsolutePath()+":" + t.toString());
				}
				jars.add(file.getPath());
				return;
			}
			if (!file.getName().endsWith(".lava")) {
				return;
			}

			Code code = new Code(topFile.getAbsolutePath() , file.getAbsolutePath(), null);
			codes.put(code.getIdName(), code);
			if (code.getIdName().endsWith(".export")) {
				modules.put(code.getIdName().substring(0, code.getIdName().length() - 7), code);
			}
			tempList.add(code);
		}

	};
	public static List<Code> initSource(List<String> codePath) {
		tempList.clear();
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
				return path.trim().length() <= 0;
			}

		});

		for (String path : paths) {
			urls.add(path);
			FileUtil.traverseFolder(path, action);
		}

		return new ArrayList<Code>(tempList);
	}

}
