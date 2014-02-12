package cz.cokrtvac.webgephi.webgephiserver.core.util;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4/23/13
 * Time: 7:17 PM
 */

import java.io.*;

/**
 * Methods to write/read/copy files
 */
public class IOUtils {
    public static byte[] read(File file) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        copy(is, os);

        byte[] out = os.toByteArray();
        is.close();
        os.close();
        return out;
    }

    public static void write(File file, InputStream data) throws IOException {
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        copy(data, os);
        os.close();
    }

    public static void write(File file, byte[] content) throws IOException {
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        os.write(content);
        os.close();
    }

    public static void copy(File from, File to) throws IOException {
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(to));
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(from));

        copy(is, os);

        is.close();
        os.close();
    }

    private static void copy(InputStream fromIS, OutputStream toOS) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = fromIS.read(buf)) > 0) {
            toOS.write(buf, 0, len);
        }
    }
}
