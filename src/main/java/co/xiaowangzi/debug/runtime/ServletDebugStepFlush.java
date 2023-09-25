package co.xiaowangzi.debug.runtime;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxFlush", loadOnStartup = 1)
public class ServletDebugStepFlush extends ServletAbstractSuccess {

    public ServletDebugStepFlush() {
        super();
    }

    @Override
    protected void doAction(HttpServletRequest request) {
        Chef.flush();
    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
