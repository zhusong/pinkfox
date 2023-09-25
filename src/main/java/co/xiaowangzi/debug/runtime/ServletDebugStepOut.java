package co.xiaowangzi.debug.runtime;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxStepOut", loadOnStartup = 1)
public class ServletDebugStepOut extends ServletAbstractSuccess {

    public ServletDebugStepOut() {
        super();
    }

    @Override
    protected void doAction(HttpServletRequest request) {
        Chef.stepOut();
    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
