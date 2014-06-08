package cz.cokrtvac.webgephi.client;

import cz.cokrtvac.webgephi.api.util.IOUtil;

import java.io.IOException;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8.2.14
 * Time: 21:20
 */
public class TestsUtil {
    public static String getTwitterGEXF() throws IOException {
        return IOUtil.readAsString(TestsUtil.class.getResourceAsStream("/twitter.gexf"));
    }

    public static String getMisserablesGEXF() throws IOException {
        return IOUtil.readAsString(TestsUtil.class.getResourceAsStream("/misserables.gexf"));
    }

    public static String getLayoutRotate() throws IOException {
        return IOUtil.readAsString(TestsUtil.class.getResourceAsStream("/layoutRotate.xml"));
    }

    public static String getStatisticClusteringCoeficient() throws IOException {
        return IOUtil.readAsString(TestsUtil.class.getResourceAsStream("/statisticClusteringCoeficient.xml"));
    }

    public static Token getTestAccessToken() {
        return new Token("test.client", "68c17d0d-test-4b0e-bb2f-f4fe50d83704", "060a7ba5-test-4969-a832-714b05f81440", "b51b92a5-test-492f-8ad7-e9dcb459ab2e");
    }
}
