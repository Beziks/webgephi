package cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation.expression_resolver;

import cz.cokrtvac.webgephi.api.util.Log;
import org.jboss.weld.el.WeldExpressionFactory;
import org.slf4j.Logger;

import javax.el.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 27.1.14
 * Time: 20:41
 */
public class ExpressionResolver {
    private Logger log = Log.get(getClass());
    private static ExpressionFactory expressionFactory = WeldExpressionFactory.newInstance();

    private Map<Object, Object> userMap = new HashMap<Object, Object>();
    private final DemoFunctionMapper functionMapper = new DemoFunctionMapper();
    private final VariableMapper variableMapper = new DemoVariableMapper();
    private final CompositeELResolver compositeELResolver;
    private ELContext context;

    public ExpressionResolver() {
        //createXml the context
        ELResolver demoELResolver = new DemoELResolver(userMap);

        compositeELResolver = new CompositeELResolver();
        compositeELResolver.add(demoELResolver);
        compositeELResolver.add(new ArrayELResolver());
        compositeELResolver.add(new ListELResolver());
        compositeELResolver.add(new BeanELResolver());
        compositeELResolver.add(new MapELResolver());
        context = new ELContext() {
            @Override
            public ELResolver getELResolver() {
                return compositeELResolver;
            }

            @Override
            public FunctionMapper getFunctionMapper() {
                return functionMapper;
            }

            @Override
            public VariableMapper getVariableMapper() {
                return variableMapper;
            }
        };
    }

    public void addVariable(String name, Object object) {
        userMap.put(name, object);
    }

    public void addFunction(String name, Method method) {
        functionMapper.addFunction(name, method);
    }

    public Object resolve(String expr) {
        return resolve(expr, Object.class);
    }

    public <T> T resolve(String expr, Class<T> resultType) {
        log.info("Resolving expression: " + expr);
        ValueExpression ve = expressionFactory.createValueExpression(context, expr, resultType);
        T result = (T) ve.getValue(context);
        log.info("Expression result: " + result);
        return result;
    }
}
