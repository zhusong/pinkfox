package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.LightQuery;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxLightQuery", loadOnStartup = 1)
public class ServletDebugLightQuery extends ServletAbstractSuccess {

    public ServletDebugLightQuery() {
        super();
    }

    @Override
    protected void doAction(HttpServletRequest request) {

    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        LightQuery lightQuery = new LightQuery();
        lightQuery.setChanging(RuntimeContext.isChanging);
        return lightQuery;
    }
}
