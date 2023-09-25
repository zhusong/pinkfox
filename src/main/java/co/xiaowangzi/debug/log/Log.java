package co.xiaowangzi.debug.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    private enum LogLevel {
        debug, info, warn, error
    }

    //全局的运行日志
    private static final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private static PrintStream logWriter;

    static {
        try {
            logWriter = new PrintStream(bos, false, "UTF-8");
        } catch (UnsupportedEncodingException e) { }
    }

    public static void info(String str){
        println(str, null, LogLevel.info);
    }

    public static void info(String str, Throwable e){
        println(str, e, LogLevel.info);
    }

    public static void error(String str){
        println(str, null, LogLevel.error);
    }

    public static void error(String str, Throwable e){
        println(str, e, LogLevel.error);
    }

    public static void warn(String str){
        println(str, null, LogLevel.warn);
    }

    public static void warn(String str, Throwable e){
        println(str, e, LogLevel.warn);
    }

    public static void debug(String str){
        println(str, null, LogLevel.debug);
    }

    public static void debug(String str, Throwable e){
        println(str, e, LogLevel.debug);
    }

    private static void println(String str, Throwable e, LogLevel level){
        LocalDateTime localDateNow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String localDateNowStr = localDateNow.format(formatter);
        logWriter.println(localDateNowStr + "   [" + level.name() + "]  " + Thread.currentThread().getName() + "------" + str);
        if(e != null){
            e.printStackTrace(logWriter);
        }
    }

    public static String queryDebugRuntimeLog(){
        if (bos.size() > 0) {
            String queryDebugRuntimeLog = bos.toString();
            bos.reset();
            return new String(queryDebugRuntimeLog.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

}
