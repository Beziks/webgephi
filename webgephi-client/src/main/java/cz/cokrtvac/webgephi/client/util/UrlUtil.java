package cz.cokrtvac.webgephi.client.util;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 20:51
 */
public class UrlUtil {
    public static String concat(String base, String path) {
        if (!base.endsWith("/")) {
            base = base + "/";
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return base + path;
    }
}
