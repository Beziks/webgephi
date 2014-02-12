package cz.cokrtvac.webgephi.webgephiserver.core.util;

import org.apache.commons.codec.binary.Base64;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 24.1.14
 * Time: 20:26
 */
public class Base64Util {
    public static void main(String[] args){
        Base64 base64 = new Base64();
        String decoded = new String(base64.encode("admin:password".getBytes()));
        System.out.println(decoded);
    }
}
