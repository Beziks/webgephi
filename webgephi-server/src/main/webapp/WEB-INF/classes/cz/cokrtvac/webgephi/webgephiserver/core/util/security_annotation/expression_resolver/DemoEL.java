package cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation.expression_resolver;

/**
 * A simple application that demonstrates the use of the
 * Unified Expression Language.
 *
 * @author McDowell
 */
public class DemoEL {

    /**
     * takes the javax.el.ExpressionFactory implementation class as an argument
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        ExpressionResolver resolver = new ExpressionResolver();
        resolver.addVariable("a", 125);
        resolver.addVariable("b", 1000);
        resolver.addVariable("xxx", 1000);
        resolver.addVariable("1", 8);
        resolver.addVariable("2", 9);
        resolver.addFunction("secti", DemoEL.class.getMethod("sum", new Class[]{int.class, int.class}));
        resolver.addFunction("concat", DemoEL.class.getMethod("concat", new Class[]{String.class, String.class}));

        resolver.resolve("${a}");
        resolver.resolve("#{2}");
        resolver.resolve("#{secti(a, b)}");

        resolver.resolve("#{concat(a, b)}");
    }

    public static int sum(int x, int y) {
        return x + y;
    }

    public static String concat(String x, String y) {
        return x + y;
    }
}
