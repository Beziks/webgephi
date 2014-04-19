package cz.cokrtvac.webgephi.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 20:51
 */
public class UrlUtil {
    private static Logger log = LoggerFactory.getLogger(UrlUtil.class);

    public static String concat(String base, String path) {
        if (!base.endsWith("/")) {
            base = base + "/";
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return base + path;
    }

    public static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            return s;
        }
    }
}
