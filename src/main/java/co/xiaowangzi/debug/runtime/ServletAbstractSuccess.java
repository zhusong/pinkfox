package co.xiaowangzi.debug.runtime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class ServletAbstractSuccess extends HttpServlet {

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Authorization,Origin,X-Requested-With,Content-Type,Accept,"
                + "content-Type,origin,x-requested-with,content-type,accept,authorization,token,id,X-Custom-Header,X-Cookie,Connection,User-Agent,Cookie,*");
        response.setHeader("Access-Control-Request-Headers", "Authorization,Origin, X-Requested-With,content-Type,Accept");
        response.setHeader("Access-Control-Expose-Headers", "*");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


        doAction(request);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        Food food = Food.success(this.successData(request, response));
        String json = food.toJson();
        out.print(json);
        out.flush();
    }

    protected abstract void doAction(HttpServletRequest request);

    protected abstract Object successData(HttpServletRequest request, HttpServletResponse response);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
