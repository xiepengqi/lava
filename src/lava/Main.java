package lava;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import lava.constant.Constants;
import lava.core.Code;
import lava.core.Sub;
import lava.util.FileUtil;
import lava.util.JavaUtil;
import lava.util.Util;

public class Main {
	public static final Map<String, Code> codes = new HashMap<String, Code>();
	public static final Map<String, String> config = new HashMap<String, String>();
	public static boolean syntaxError = false;
	public static final List<String> ARGS = new ArrayList<String>();
	public static final Map<String, Sub> subs = new HashMap<String, Sub>();
 
	public static boolean repl = false;

	public static void main(String[] args) throws Exception {
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
		initSourcePath.add("/ext/lava");
		for (String key : envs.keySet()) {
			if (key.toLowerCase().startsWith("lavapath")) {
				initSourcePath.add(envs.get(key));
			}
		}

		initSource(initSourcePath);

		if (codePaths.size() == 0) {
			startRepl();
			return;
		}

		List<Code> mainCodes = initSource(codePaths);

		for (Code code : mainCodes) {
			code.parse();
			code.check();
			code.run();
		}
	}

	public static void startRepl() throws IOException, Exception {
		Main.repl = true;
		Code code = new Code("lava.repl", null);
		Main.codes.put(code.getIdName(), code);

		String line = "(/repl (if (def? $-1) $-1  ''))";
		code.eval(line);

		Scanner scanner = new Scanner(System.in);
		while (line != null) {
			System.out.print(Constants.replPrefix);
			line = scanner.nextLine();
			while (line.endsWith(Constants.replPrefix)) {
				line += scanner.nextLine();
			}
			if (line.length() == 0) {
				continue;
			}
			try {
				System.out
						.println(code.eval("(repl " + line + " )").getValue());
			} catch (Exception e) {
				e.printStackTrace();
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
					JavaUtil.loadjar(file);
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
					list.add(codes.get(idName));
				} else {
					Code code = new Code(idName, file.getAbsolutePath());
					codes.put(idName, code);
					list.add(code);
				}
			}

			private String getIdName(String homePath, String filePath) {
				return filePath.substring(homePath.length()+1).replaceAll("\\.[^/\\.]+$", "");
			}

		};

		for (String path : paths) {
			FileUtil.traverseFolder(path, action);
		}
		return list;
	}
}
