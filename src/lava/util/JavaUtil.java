package lava.util;

import lava.constant.Constants;
import lava.core.Data;
import lava.core.Form;
import lava.core.JarLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JavaUtil {
	private static final JarLoader jarLoader = new JarLoader();

	public static void loadJar(URL url) throws Exception {
		Method addURL = URLClassLoader.class.getDeclaredMethod("addURL",
				URL.class);
		addURL.setAccessible(true);
		addURL.invoke(jarLoader, url);
	}

	public static Class forName(String className) throws ClassNotFoundException {
		return Class.forName(className, true, jarLoader);
	}

	public static HashMap<String, Class> getJarLoaderClass() throws Exception {
		final HashMap<String, Class> classMap = new HashMap<String, Class>();

		for (URL url : jarLoader.getURLs()) {
			String filePath=URLDecoder.decode(url.getFile(), "utf-8");
			if (new File(filePath).isDirectory()) {
				FileUtil.traverseFolder(filePath, new FileUtil.Action() {
					public void action(File topFile, File file) {
						if (file.getName().endsWith(".jar")) {
							HashMap<String, Class> map = getJarClass(
									file.getPath(), jarLoader);
							if (map != null) {
								classMap.putAll(map);
							}
						}
					}
				});
			} else {
				HashMap<String, Class> map = getJarClass(filePath,
						jarLoader);
				if (map != null) {
					classMap.putAll(map);
				}
			}
		}

		return classMap;
	}

	private static HashMap<String, Class> getJarClass(String jarPath,
			ClassLoader classLoader) {
		HashMap<String, Class> classMap = new HashMap<String, Class>();
		JarFile jar = null;
		try {
			jar = new JarFile(jarPath);
		} catch (IOException e1) {
			return null;
		}
		Enumeration e = jar.entries();
		while (e.hasMoreElements()) {
			JarEntry entry = (JarEntry) e.nextElement();
			if (entry.getName().indexOf("META-INF") >= 0) {
				continue;
			}
			String sName = entry.getName();
			if (sName.endsWith(".class")){
				String ppName = sName.replace("/", ".").replace(".class", "");
				Class myClass = null;
				try {
					myClass = classLoader.loadClass(ppName);
				} catch(Throwable t) {
				}
				classMap.put(ppName, myClass);
			}
		}
		return classMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Data processMethod(String fnName, List<Data> args)
			throws Exception {
		List<Object> values = new ArrayList<Object>();
		List<Class> types = new ArrayList<Class>();

		Util.splitArgs(args, values, types);

		String methodStr = null;
		Object object = null;
		Class classObject = null;
		if (fnName.startsWith(Constants.javaChar)) {
			methodStr = fnName.substring(1);

			object = values.get(0);
			classObject = Data.getClass(object);

			values = values.subList(1, values.size());
			types = types.subList(1, types.size());
		} else if (fnName.contains(Constants.javaChar)) {
			methodStr = StringUtil.getFirstMatch("[^\\.]+$", fnName);
			String className = fnName.replaceFirst("\\.[^\\.]+$", "");
			object = null;
			classObject = forName(className);
		}

		Method method = null;
		Exception ex = null;
		while (classObject!=null) {
			try {
				method = classObject.getDeclaredMethod(methodStr,
						types.toArray(new Class[types.size()]));
				classObject = null;
			} catch (NoSuchMethodException e) {
				if(ex == null) {
					ex = e;
				}
				classObject = classObject.getSuperclass();
			}
		}
		if (method ==null && ex !=null) {
			throw ex;
		}
		method.setAccessible(true);
		return new Data(method.getReturnType(), method.invoke(object, values.toArray()));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object processNew(List<Data> args)
			throws Exception {
		List<Object> values = new ArrayList<Object>();
		List<Class> types = new ArrayList<Class>();

		Util.splitArgs(args, values, types);

		Class classObj = forName((String) values.get(0));
		Constructor con = null;
		Exception ex = null;
		Class typeTemp = null;
		while (classObj !=null) {
			try {
				con = classObj.getConstructor(types.subList(1, args.size())
						.toArray(new Class[types.size() - 1]));
				typeTemp = classObj;
				classObj = null;
			} catch (NoSuchMethodException e) {
				if(ex == null) {
					ex = e;
				}
				classObj = classObj.getSuperclass();
			}
		}
		if (con ==null && ex !=null) {
			throw ex;
		}
		con.setAccessible(true);
		return new Data(typeTemp, con.newInstance(values.subList(1, values.size()).toArray()));
	}

	@SuppressWarnings("rawtypes")
	public static Data processField(List<Data> args)
			throws Exception {
		Class classObj;
		Object obj;
		String fieldStr;
		List<Object> values = new ArrayList<Object>();
		List<Class> types = new ArrayList<Class>();
		Util.splitArgs(args, values, types);

		if (args.size() > 1) {
			obj = values.get(0);
			classObj = Data.getClass(obj);
			fieldStr = (String) values.get(1);
		} else {
			String className = ((String) values.get(0)).replaceFirst(
					"\\.[^\\.]+$", "");
			obj = null;
			classObj = forName(className);
			fieldStr = StringUtil.getFirstMatch("[^\\.]+$",(String) values.get(0));
		}
		Field field = null;
		Exception ex = null;
		Class typeTemp = null;
		while (classObj !=null) {
			try {
				field =  classObj.getDeclaredField(fieldStr);
				typeTemp = classObj;
				classObj = null;
			} catch (NoSuchFieldException e) {
				if(ex == null) {
					ex = e;
				}
				classObj = classObj.getSuperclass();
			}
		}
		if (field ==null && ex !=null) {
			throw ex;
		}
		field.setAccessible(true);
		return new Data(typeTemp, field.get(obj));
	}

}
