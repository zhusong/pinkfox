package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.BreakPoint;
import co.xiaowangzi.debug.clazz.Clazz;
import co.xiaowangzi.debug.log.Log;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/pinkfoxDump", loadOnStartup = 1)
public class ServletDebugDump extends ServletAbstractSuccess {

    @Override
    protected void doAction(HttpServletRequest request) {

    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        try {
            ByteArrayOutputStream dumpStream = new ByteArrayOutputStream();
            PrintStream dumpStreamWriter = new PrintStream(dumpStream);
            //当前debug的类信息
            //所有类的信息
            //
            if(RuntimeContext.debuging){
                dumpStreamWriter.println("当前处于debuging中：" + true);
            } else {
                dumpStreamWriter.println("当前处于debuging中：" + false);
            }
            if(RuntimeContext.muteAllBreakPoints){
                dumpStreamWriter.println("当前处于mute中：" + true);
            } else{
                dumpStreamWriter.println("当前处于mute中：" + false);
            }
            if(RuntimeContext.debuging){
                dumpStreamWriter.println("debuging中的类信息如下：");
                this.writeClazz(RuntimeContext.lastBreakPoint.getClazz(), dumpStreamWriter);
            }
            dumpStreamWriter.println("所有类信息如下：");
            for(Map.Entry<String, Clazz> entry : RuntimeContext.clazzMap.entrySet()){
                this.writeClazz(entry.getValue(), dumpStreamWriter);
            }
            // 以流的形式下载文件。
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=dump.txt");
            response.addHeader("Content-Length", "" + dumpStream.size());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(dumpStream.toByteArray());
            toClient.flush();
            toClient.close();
        } catch (IOException e) {
            Log.error("Dump error", e);
        }
        return null;
    }

    public void writeClazz(Clazz clazz, PrintStream printStream){
        printStream.println("class-" + clazz.getClassFullQualifiedName());
        printStream.println("------parent-" + clazz.getParentClassFullQualifiedName());
        printStream.println("------runtime-breakpoints-size-" + clazz.getRuntimeBreakPoints().size());
        for(BreakPoint breakPoint : clazz.getRuntimeBreakPoints()){
            printStream.println("------------index-" + breakPoint.getPointIndex());
            printStream.println("------------methodName-" + breakPoint.getMethodName());
            printStream.println("------------shouldRun-" + breakPoint.getShouldRun());
            printStream.println("------------innerClassname-" + breakPoint.getInnerClassName());
            printStream.println("------------staticMethod-" + breakPoint.getStaticMethod());
            printStream.println("------------methodIndex-" + breakPoint.getMethodIndex());
        }
        printStream.println("------class-fields-size-" + clazz.getFields().size());
        for(String fieldName : clazz.getFields()){
            printStream.println("------------" + fieldName);
        }
        printStream.println("------break-point-index-variables-size-" + clazz.getBreakPointVariables().size());
        for(int i = 0; i < clazz.getBreakPointVariables().size(); i++){
            printStream.println("------------break-point-index-variables-" + i);
            List<String> breakPointVariables = clazz.getBreakPointVariables().get(i);
            for(String variable : breakPointVariables){
                printStream.println("------------------" + variable);
            }
        }
    }
}
