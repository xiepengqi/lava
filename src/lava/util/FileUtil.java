package lava.util;

import lava.Main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;

public class FileUtil {

	public static class Action {
		public void action(File topFile, File file) {

		}
	}

	public static void copy(Reader in, Writer out) throws IOException {
		int c = -1;
		while ((c = in.read()) != -1) {
			out.write(c);
		}
	}

	public static String readFile(String filePath) throws IOException {
		return readFile(new File(filePath));
	}

	public static String readFile(File file) throws IOException {
		return readStream(new FileReader(file));
	}

	public static String readStream(InputStream in) throws IOException {
		return readStream(new InputStreamReader(in));
	}

	public static String readStream(Reader in) throws IOException {
		StringWriter out = new StringWriter();
		copy(in, out);
		return out.toString();
	}

	public static void traverseFolder(String path, Action action) {
		File file;
		try{
			file=new File(Main.class.getResource(path).getFile());
		}catch(Exception e){
			file = new File(path);
		}
		if (file.exists()) {
			LinkedList<File> list = new LinkedList<File>();
			File[] files = file.listFiles();
			if (null == files) {
				files = new File[] { file };
			}
			for (File file2 : files) {
				if (file2.isDirectory()) {
					list.add(file2);
				} else {
					action.action(file, file2);
				}
			}
			File temp_file;
			while (!list.isEmpty()) {
				temp_file = list.removeFirst();
				files = temp_file.listFiles();
				for (File file2 : files) {
					if (file2.isDirectory()) {
						list.add(file2);
					} else {
						action.action(file, file2);
					}
				}
			}
		}

	}
}
