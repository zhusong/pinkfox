package co.xiaowangzi.debug.utils;

import java.util.HashMap;
import java.util.Map;

public class Id {

    private static int id = 0;
    private static Map<String, Integer> idClazzMap = new HashMap<>();

    public synchronized static int generateId(String incrementalPath){
        final int generated = ++id;
        if(incrementalPath.endsWith(".java")){
            final int startIndex = incrementalPath.indexOf("src/main/java/") + "src/main/java/".length();
            final String clazzWithEndFix = incrementalPath.substring(startIndex);
            final String classFullQualifiedName = clazzWithEndFix.replace(".java", "").replaceAll("/",".");
            idClazzMap.put(classFullQualifiedName, generated);
        }
        return generated;
    }

    public synchronized static int getId(String classFullQualifiedName){
        return idClazzMap.get(classFullQualifiedName);
    }
}
