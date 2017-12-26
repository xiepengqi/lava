package lava.util;

import lava.constant.Constants;
import lava.core.DataMap.DataInfo;
import lava.core.Form;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class JavaUtil {
	public static JarLoader	jarLoader	= new JarLoader((URLClassLoader) ClassLoader.getSystemClassLoader());

	public static class JarLoader {
		private URLClassLoader	urlClassLoader;

		public JarLoader(URLClassLoader urlClassLoader) {
			this.urlClassLoader = urlClassLoader;
		}

		public void loadJar(URL url) throws Exception {
			Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			addURL.setAccessible(true);
			addURL.invoke(urlClassLoader, url);
		}
	}

	public static void loadjar(File jarFile) throws Exception {
		jarLoader.loadJar(jarFile.toURI().toURL());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object processMethod(Form form, List<DataInfo> args) throws Exception {
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

		method = classObject.getMethod(methodStr, types.toArray(new Class[types.size()]));
		if (method == null) {
			method = classObject.getDeclaredMethod(methodStr, types.toArray(new Class[types.size()]));
		}
		method.setAccessible(true);
		form.setType(method.getReturnType());
		return method.invoke(object, values.toArray());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object processNew(Form form, List<DataInfo> args) throws Exception {
		List<Object> values = new ArrayList<Object>();
		List<Class> types = new ArrayList<Class>();

		Util.splitArgs(args, values, types);

		Class classObj = Class.forName((String) values.get(0));
		Constructor con = classObj.getConstructor(types.subList(1, args.size()).toArray(new Class[types.size() - 1]));
		if (con == null) {
			con = classObj.getDeclaredConstructor(types.subList(1, args.size()).toArray(new Class[types.size() - 1]));
		}
		con.setAccessible(true);
		form.setType(classObj);
		return con.newInstance(values.subList(1, values.size()).toArray());
	}

	@SuppressWarnings("rawtypes")
	public static Object processField(Form form, List<DataInfo> args) throws Exception {
		Class classObj;
		Object obj;
		Field field;
		List<Object> values = new ArrayList<Object>();

		Util.splitArgs(args, values, null);

		if (args.size()>1) {
			obj = values.get(0);
			classObj = obj.getClass();
			field=getField(classObj,(String)values.get(1));
		}else{
			String className = ((String)values.get(0)).replaceFirst("\\.[^\\.]+$", "");
			obj = null;
			classObj = Class.forName(className);
			field=getField(classObj,StringUtil.getFirstMatch("[^\\.]+$", (String)values.get(0)));
		}

		field.setAccessible(true);
		form.setType(field.getType());
		return field.get(obj);
	}

	public static Field getField(Class classObj,String fieldName) throws NoSuchFieldException {
		Field field = classObj.getField(fieldName);
		if (field == null) {
			field = classObj.getDeclaredField(fieldName);
		}
		return field;
	}

}
