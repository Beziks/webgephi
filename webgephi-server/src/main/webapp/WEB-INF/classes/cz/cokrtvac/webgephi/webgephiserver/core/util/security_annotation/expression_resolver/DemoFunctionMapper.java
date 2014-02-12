package cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation.expression_resolver;


import javax.el.FunctionMapper;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author McDowell
 */
public class DemoFunctionMapper extends FunctionMapper {

    private Map<String, Method> functionMap = new HashMap<String, Method>();

    @Override
    public Method resolveFunction(String prefix, String localName) {
        if(prefix == null || prefix.isEmpty()){
            return functionMap.get(localName);
        }
        String key = prefix + ":" + localName;
        return functionMap.get(key);
    }

    public void addFunction(String name, Method method){
        if (name == null || method == null) {
            throw new NullPointerException();
        }

        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("method not public");
        }
        if (!Modifier.isStatic(modifiers)) {
            throw new IllegalArgumentException("method not static");
        }
        Class<?> retType = method.getReturnType();
        if (retType == Void.TYPE) {
            throw new IllegalArgumentException("method returns void");
        }

        functionMap.put(name, method);
    }

    public void addFunction(String prefix, String localName, Method method) {
        if (prefix == null || localName == null || method == null) {
            throw new NullPointerException();
        }
        addFunction(prefix + ":" + localName, method);
    }

}
