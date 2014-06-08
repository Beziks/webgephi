package cz.cokrtvac.webgephi.api.util;

import java.io.*;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8.2.14
 * Time: 21:29
 */
public class IOUtil {
    public static byte[] read(File file) throws IOException {
        return read(new FileInputStream(file));
    }

    public static String readAsString(InputStream inputStream) throws IOException {
        return new String(read(inputStream));
    }

    public static byte[] read(InputStream fromIs) throws IOException {
        BufferedInputStream is = null;
        ByteArrayOutputStream os = null;

        try {
            is = new BufferedInputStream(fromIs);
            os = new ByteArrayOutputStream();

            copy(is, os);

            byte[] out = os.toByteArray();
            return out;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void write(File file, InputStream data) throws IOException {
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            copy(data, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void write(File file, byte[] content) throws IOException {
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            os.write(content);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void copy(File from, File to) throws IOException {
        BufferedOutputStream os = null;
        BufferedInputStream is = null;

        try {
            os = new BufferedOutputStream(new FileOutputStream(to));
            is = new BufferedInputStream(new FileInputStream(from));
            copy(is, os);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void copy(InputStream fromIS, OutputStream toOS) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = fromIS.read(buf)) > 0) {
            toOS.write(buf, 0, len);
        }
    }
}
