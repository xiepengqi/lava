package lava.core;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by xie,pengqi on 2018/3/23.
 */
public class JarLoader extends URLClassLoader {
    public JarLoader() {
        super(new URL[] {}, findParentClassLoader());
    }

    private static ClassLoader findParentClassLoader() {
        ClassLoader parent = JarLoader.class.getClassLoader();
        if (parent == null) {
            parent = JarLoader.class.getClassLoader();
        }
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        return parent;
    }
}
