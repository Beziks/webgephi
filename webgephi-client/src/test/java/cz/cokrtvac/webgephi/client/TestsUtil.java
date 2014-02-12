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
        return IOUtil.readFile(TestsUtil.class.getResourceAsStream("/twitter.gexf"));
    }

    public static String getMisserablesGEXF() throws IOException {
        return IOUtil.readFile(TestsUtil.class.getResourceAsStream("/misserables.gexf"));
    }

    public static String getLayoutRotate() throws IOException {
        return IOUtil.readFile(TestsUtil.class.getResourceAsStream("/layoutRotate.xml"));
    }

    public static String getStatisticClusteringCoeficient() throws IOException {
        return IOUtil.readFile(TestsUtil.class.getResourceAsStream("/statisticClusteringCoeficient.xml"));
    }
}
