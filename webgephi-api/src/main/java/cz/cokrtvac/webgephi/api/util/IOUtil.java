package cz.cokrtvac.webgephi.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8.2.14
 * Time: 21:29
 */
public class IOUtil {
    public static String readFile(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder s = new StringBuilder();
        String tmp = null;
        while ((tmp = br.readLine()) != null) {
            s.append(tmp).append("\n");
        }
        return s.toString();
    }
}
