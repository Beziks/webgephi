package cz.cokrtvac.webgephi.api.util;

import cz.cokrtvac.webgephi.api.UnexpectedException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 9.6.13
 * Time: 17:34
 */
public class StringUtil {
    private static Logger LOG = Log.get(StringUtil.class);

    public static String uriSafe(String name) {
        name = name.replaceAll(" ", "-");
        name = name.toLowerCase();
        name = removeDiaeresis(name);
        try {
            name = URIUtil.encodeQuery(name);
        } catch (URIException e) {
            String msg = "Converting from to uri failed";
            LOG.error(msg, e);
            throw new UnexpectedException(msg, e);
        }
        return name;
    }

    private static String removeDiaeresis(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(s).replaceAll("");
    }

    public static String splitCamelCase(String s) {
        StringBuilder sb = new StringBuilder();
        for (Character c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append(" ");
            }
            sb.append(c);
        }
        return sb.toString().trim();
    }
}
