package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.JsonPrintBridge;
import co.xiaowangzi.debug.utils.Pair;
import co.xiaowangzi.debug.utils.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxVariableJsonPrint", loadOnStartup = 1)
public class ServletDebugVariableJsonPrint extends ServletAbstractSuccess {

    public ServletDebugVariableJsonPrint() {
        super();
    }

    @Override
    protected void doAction(HttpServletRequest request) {

    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        final String variable = request.getParameter("variable");
        if(StringUtils.isEmpty(variable)){
            return null;
        }
        Pair<Boolean, String> result = Chef.debugVariableJsonPrint(variable);
        JsonPrintBridge jsonPrintBridge = new JsonPrintBridge();
        jsonPrintBridge.setJsonValue(result.getValue());
        jsonPrintBridge.setJsonGood(result.getKey());
        return jsonPrintBridge;
    }
}
