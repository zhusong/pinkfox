package co.xiaowangzi.debug.runtime;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxResume", loadOnStartup = 1)
public class ServletDebugResume extends ServletAbstractSuccess {

    public ServletDebugResume() {
        super();
    }

    @Override
    protected void doAction(HttpServletRequest request) {
        Chef.resume();
    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
