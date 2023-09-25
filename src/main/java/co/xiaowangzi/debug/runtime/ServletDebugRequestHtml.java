package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.Clazz;
import co.xiaowangzi.debug.clazz.RequestHtmlBridge;
import co.xiaowangzi.debug.utils.Id;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxRequestHtml", loadOnStartup = 1)
public class ServletDebugRequestHtml extends ServletAbstractSuccess {

    public ServletDebugRequestHtml() {
        super();
    }

    @Override
    protected void doAction(HttpServletRequest request) {

    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
//        final String requestNode = request.getParameter("requestNode");
//        final int index = requestNode.indexOf("src.main.java.") + "src.main.java.".length();
//        final String classFullQualifiedName = requestNode.substring(index).replace(".java", "").replaceAll("/",".");;
        final String classFullQualifiedName = request.getParameter("classFullQualifiedName");
        Clazz clazz = RuntimeContext.clazzMap.get(classFullQualifiedName);
        RequestHtmlBridge requestHtmlBridge = new RequestHtmlBridge();
        requestHtmlBridge.setHtmlSource(SourceFormatter.format(clazz));
        requestHtmlBridge.setId(Id.getId(classFullQualifiedName));
        return requestHtmlBridge;
    }
}
