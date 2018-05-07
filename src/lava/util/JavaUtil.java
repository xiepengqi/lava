package lava.util;

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

import lava.constant.Constants;
import lava.core.Data;
import lava.core.Form;

public class JavaUtil {
	
	public static HashMap<String, Class> getJarLoaderClass(
			final URLClassLoader classLoader) throws Exception {
		final HashMap<String, Class> classMap = new HashMap<String, Class>();

		for (URL url : classLoader.getURLs()) {
			String filePath=URLDecoder.decode(url.getFile());
			if (new File(filePath).isDirectory()) {
				FileUtil.traverseFolder(filePath, new FileUtil.Action() {
					public void action(File topFile, File file) {
						if (file.getName().endsWith(".jar")) {
							HashMap<String, Class> map = getJarClass(
									file.getPath(), classLoader);
							if (map != null) {
								classMap.putAll(map);
							}
						}
					}
				});
			} else {
				HashMap<String, Class> map = getJarClass(filePath,
						classLoader);
				if (map != null) {
					classMap.putAll(map);
				}
			}
		}

		return classMap;
	}

	public static HashMap<String, Class> getJarClass(String jarPath,
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
	public static Object processMethod(Form form, List<Data> args)
			throws Exception {
		List<Object> values = new ArrayList<Object>();
		List<Class> types = new ArrayList<Class>();

		Util.splitArgs(args, values, types);

		Method method = null;
		String fnName = form.getFnName();
		String methodStr = null;
		Object object = null;
		Class classObject = null;
		if (fnName.startsWith(Constants.javaChar)) {
			methodStr = fnName.substring(1);

			object = values.get(0);
			classObject = object.getClass();

			values = values.subList(1, values.size());
			types = types.subList(1, types.size());
		} else if (fnName.contains(Constants.javaChar)) {
			methodStr = StringUtil.getFirstMatch("[^\\.]+$", fnName);
			String className = fnName.replaceFirst("\\.[^\\.]+$", "");
			object = null;
			classObject = Class.forName(className);
		}

		try {
			method = classObject.getMethod(methodStr,
					types.toArray(new Class[types.size()]));
		} catch (NoSuchMethodException e) {
		}
		if (method == null) {
			method = classObject.getDeclaredMethod(methodStr,
					types.toArray(new Class[types.size()]));
		}
		method.setAccessible(true);
		form.setType(method.getReturnType());
		return method.invoke(object, values.toArray());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object processNew(Form form, List<Data> args)
			throws Exception {
		List<Object> values = new ArrayList<Object>();
		List<Class> types = new ArrayList<Class>();

		Util.splitArgs(args, values, types);

		Class classObj = Class.forName((String) values.get(0));
		Constructor con = null;
		try {
			con = classObj.getConstructor(types.subList(1, args.size())
					.toArray(new Class[types.size() - 1]));
		} catch (NoSuchMethodException e) {
		}
		if (con == null) {
			con = classObj.getDeclaredConstructor(types.subList(1, args.size())
					.toArray(new Class[types.size() - 1]));
		}
		con.setAccessible(true);
		form.setType(classObj);
		return con.newInstance(values.subList(1, values.size()).toArray());
	}

	@SuppressWarnings("rawtypes")
	public static Object processField(Form form, List<Data> args)
			throws Exception {
		Class classObj;
		Object obj;
		Field field;
		List<Object> values = new ArrayList<Object>();

		Util.splitArgs(args, values, null);

		if (args.size() > 1) {
			obj = values.get(0);
			classObj = obj.getClass();
			field = getField(classObj, (String) values.get(1));
		} else {
			String className = ((String) values.get(0)).replaceFirst(
					"\\.[^\\.]+$", "");
			obj = null;
			classObj = Class.forName(className);
			field = getField(classObj, StringUtil.getFirstMatch("[^\\.]+$",
					(String) values.get(0)));
		}

		field.setAccessible(true);
		form.setType(field.getType());
		return field.get(obj);
	}

	public static Field getField(Class classObj, String fieldName)
			throws NoSuchFieldException {
		Field field = null;
		try {
			field = classObj.getField(fieldName);
		} catch (NoSuchFieldException e) {
		}

		if (field == null) {
			field = classObj.getDeclaredField(fieldName);
		}
		return field;
	}

}
