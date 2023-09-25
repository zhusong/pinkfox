package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.Clazz;
import co.xiaowangzi.debug.utils.CollectionUtils;
import co.xiaowangzi.debug.utils.Id;
import co.xiaowangzi.debug.utils.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/pinkfoxRequestExecutingContext", loadOnStartup = 1)
public class ServletDebugExecutingContext extends ServletAbstractSuccess {

    public ServletDebugExecutingContext() {
        super();
    }

    @Override
    protected void doAction(HttpServletRequest request) {

    }

    @Override
    protected Object successData(HttpServletRequest request, HttpServletResponse response) {
        String classFullQualifiedName = request.getParameter("classFullQualifiedName");

        KitchenContext kitchenContext = new KitchenContext();
        kitchenContext.setDebuging(RuntimeContext.isDebuging());
        kitchenContext.setBreakPointsMute(RuntimeContext.muteAllBreakPoints);
        kitchenContext.setChanging(Chef.isBreakPointChanged());
        if(kitchenContext.getChanging()) {
            //如果在debug中，并且breakPoint发生变化了，则，
            kitchenContext.setDebugingClass(RuntimeContext.lastBreakPoint.getClassFullQualifiedName());
            kitchenContext.setDebugingPointIndex(RuntimeContext.lastBreakPoint.getPointIndex());
            //默认展开的菜单
            kitchenContext.setDebugingClazzTreeNodeId(Id.getId(RuntimeContext.lastBreakPoint.getClassFullQualifiedName()));
            //获取当前正在执行的断点对应的类的源码
            Clazz clazz = RuntimeContext.lastBreakPoint.getClazz();
            kitchenContext.setHtml(SourceFormatter.format(clazz));
            kitchenContext.setVariables(Chef.variables());

        } else {
            if(RuntimeContext.isDebuging() && StringUtils.isEmpty(classFullQualifiedName)){
                classFullQualifiedName = RuntimeContext.lastBreakPoint.getClassFullQualifiedName();
            }
            if(!StringUtils.isEmpty(classFullQualifiedName)) {
                //断点没有发生改变，前端请求哪个类，就展示哪个类
                kitchenContext.setHtml(SourceFormatter.source(classFullQualifiedName));
                //默认展开的菜单
                kitchenContext.setDebugingClazzTreeNodeId(Id.getId(classFullQualifiedName));
            } else {
                if(!CollectionUtils.isEmpty(Chef.runtimeBreakPointsStack)){
                    classFullQualifiedName = Chef.runtimeBreakPointsStack.get(Chef.runtimeBreakPointsStack.size() - 1).getClassFullQualifiedName();
                    kitchenContext.setHtml(SourceFormatter.source(classFullQualifiedName));
                    kitchenContext.setDebugingClazzTreeNodeId(Id.getId(classFullQualifiedName));
                }
            }
        }

        kitchenContext.setAllBreakPoints(Chef.showAllBreakPoints());
        kitchenContext.setLog(Chef.queryExecutingLog());
        return kitchenContext;
    }
}
