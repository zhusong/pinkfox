package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.Clazz;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClazzLoader {

    @SuppressWarnings("unchecked")
    public static Map<String, Clazz> loadClasses() {
        Map<String, Clazz> clazzMap = new HashMap();
        try {
            Enumeration<URL> files = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/co.xiaowangzi.debug.ClassMap");
            while (files.hasMoreElements()) {
                try {
                    URL url = files.nextElement();
                    if ("file".equals(url.getProtocol())) {
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        File file = new File(filePath);
                        FileInputStream fis = new FileInputStream(file);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        clazzMap.putAll((Map<String, Clazz>) ois.readObject());
                        ois.close();
                        fis.close();
                    } else if ("jar".equals(url.getProtocol())) {
                        try {
                            JarEntry jarEntry = ((JarURLConnection) url.openConnection()).getJarEntry();
                            JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                            InputStream inputStream = jarFile.getInputStream(jarEntry);
                            ObjectInputStream ois = new ObjectInputStream(inputStream);
                            clazzMap.putAll((Map<String, Clazz>) ois.readObject());
                            ois.close();
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazzMap;
    }
}
