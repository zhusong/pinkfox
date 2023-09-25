package co.xiaowangzi.debug.runtime;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxLog", loadOnStartup = 1)
public class ServletDebugLog extends ServletAbstractSuccess {

    public ServletDebugLog() {
        super();
    }

    @Override
    protected void doAction(HttpServletRequest request) {

    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        return Chef.queryExecutingLog();
    }
}
