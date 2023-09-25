package co.xiaowangzi.debug.runtime;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxStop", loadOnStartup = 1)
public class ServletDebugStop extends ServletAbstractSuccess {

    @Override
    protected void doAction(HttpServletRequest request) {
        Chef.stop();
    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
