package cz.cokrtvac.webgephi.webgephiserver.core.util;

import cz.cokrtvac.webgephi.api.util.IOUtil;
import org.apache.commons.codec.binary.Base64;

import java.io.File;

public class ImageUtils {

    /**
     * Encode image file to base64 string
     *
     * @return ecoded image
     */
    public static String encodePngToBase64(File file) {
        try {
            byte[] imageByte = IOUtil.read(file);

            String base64 = Base64.encodeBase64String(imageByte);
            return base64;
        } catch (Exception e) {
            return null;
        }
    }

    public static String pngToEmbededBase64(File file) {
        return "data:image/png;base64," + encodePngToBase64(file);
    }

    public static void main(String args[]) {
        System.out.println(pngToEmbededBase64(new File("C:/Users/beziks/AppData/Local/Temp/temp7513478020397302488111180292539843/eigenvector-centralities.png")));
    }
}
