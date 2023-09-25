package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.BreakPoint;
import co.xiaowangzi.debug.clazz.Clazz;
import co.xiaowangzi.debug.log.Log;
import co.xiaowangzi.debug.utils.Pair;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxAddBreakPointExpression", loadOnStartup = 1)
public class ServletDebugAddBreakPointExpression extends ServletAbstractSuccess {

    public ServletDebugAddBreakPointExpression() {
        super();
    }

    @Override
    protected void doAction(HttpServletRequest request) {
        final String classFullQualifiedName = request.getParameter("classFullQualifiedName");
        final String pointIndex = request.getParameter("pointIndex");
        final String expression = request.getParameter("expression");
        Clazz clazz = RuntimeContext.clazzMap.get(classFullQualifiedName);
        BreakPoint breakPoint = clazz.getBreakPoint(Integer.parseInt(pointIndex));
        breakPoint.setExpression(expression);
    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
