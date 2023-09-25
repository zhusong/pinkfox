package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.utils.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxSearching", loadOnStartup = 1)
public class ServletDebugSearching extends ServletAbstractSuccess {

    @Override
    protected void doAction(HttpServletRequest request) {

    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        final String query = request.getParameter("query");
        if(StringUtils.isEmpty(query)){
            return null;
        }
        return Chef.searching(query);
    }
}
