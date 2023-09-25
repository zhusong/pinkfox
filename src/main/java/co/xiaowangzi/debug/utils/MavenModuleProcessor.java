package co.xiaowangzi.debug.utils;

import com.sun.tools.javac.code.Symbol;

import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MavenModuleProcessor {

    ///Users/zhusong/Documents/A Great Start/Enjoy My Life/Learning/debug/debug-tools-web-test/src/main/java/com/jd/debug/tools/web/test/GenericTreeTraversalOrderEnum.java
    public static String mavenModuleName(Element classElement){
        Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol)classElement;
        final JavaFileObject sourceFile = classSymbol.sourcefile;
        final String filePath = sourceFile.getName();
        // 标准化路径
        String standardizedPath = filePath.replace("//", "/").replace("\\", "/");
        // 获取文件夹路径
        int index = standardizedPath.indexOf("/src/main/java");
        String folderPath = standardizedPath.substring(0, index);
        // 获取文件夹名称
        String[] folderParts = folderPath.split("/");
        return folderParts[folderParts.length - 1];
    }
}
