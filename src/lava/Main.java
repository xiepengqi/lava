package lava;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import lava.constant.Constants;
import lava.core.Code;
import lava.core.SysError;
import lava.util.FileUtil;
import lava.util.JavaUtil;
import lava.util.StringUtil;
import lava.util.Util;

public class Main {
	public static final Map<String, Code> codes = new HashMap<String, Code>();
	public static final Map<String, String> config = new HashMap<String, String>();
	public static boolean syntaxError = false;
	public static final List<String> ARGS = new ArrayList<String>();
	public static final Map<Object,Object> subLinks=new HashMap<Object,Object>();
	public static final Map<String, Class> jarClass = new HashMap<String, Class>();
	
	public static boolean debug=false;
	public static boolean repl = false;

	
	public static final JarLoader jarLoader = new JarLoader(
			(URLClassLoader) Main.class.getClassLoader());

	public static class JarLoader {
		private URLClassLoader urlClassLoader;

		public JarLoader(URLClassLoader urlClassLoader) {
			this.urlClassLoader = urlClassLoader;
		}

		public void loadJar(URL url) throws Exception {
			Method addURL = URLClassLoader.class.getDeclaredMethod("addURL",
					URL.class);
			addURL.setAccessible(true);
			addURL.invoke(urlClassLoader, url);
			
			if(repl){
				jarClass.putAll(JavaUtil.getJarClass(URLDecoder.decode(url.getFile()), jarLoader.urlClassLoader));
			}
		}
	}
	
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
		for (String key : envs.keySet()) {
			if (key.equalsIgnoreCase("lava_home")) {
				initSourcePath.add(envs.get(key) + "/lib");
			}
		}

		initSource(initSourcePath);

		if (codePaths.size() == 0) {
			try {
				jarClass.putAll(JavaUtil.getJarLoaderClass(jarLoader.urlClassLoader));
				startRepl();
			} catch (SysError e) {
				Util.systemError(e.getMessage());
			} catch (Throwable t) {
				Util.systemError("lava.repl:"+t.toString());
			}
			return;
		}

		List<Code> mainCodes = initSource(codePaths);

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

	public static void startRepl() throws Exception{
		Main.repl = true;
		Code code = new Code(null, null, "lava.repl");
		Main.codes.put(code.getIdName(), code);

		String line = "(/repl (if (def? $-1) $-1  ''))";
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
						jarLoader.loadJar(file.toURI().toURL());;
					} catch (Throwable t) {
						Util.systemError("fail to load jar file:" + file.getAbsolutePath()+":" + t.toString());
					}
					return;
				}
				if (!file.getName().endsWith(".lava")) {
					return;
				}

				Code code = new Code(topFile.getAbsolutePath() , file.getAbsolutePath(), null);
				codes.put(code.getIdName(), code);
				list.add(code);
			}

		};

		for (String path : paths) {
			FileUtil.traverseFolder(path, action);
		}
		return list;
	}

}
