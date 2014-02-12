package cz.cokrtvac.webgephi.api.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3.6.13
 * Time: 16:46
 */
public class CollectionsUtil {
    public static String toString(Iterable<?> coll) {
        StringBuilder sb = new StringBuilder(" [\n");
        for (Object o : coll) {
            sb.append("    ").append(o).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }


    public static String toString(Object[] coll) {
        return toString(Arrays.asList(coll));
    }

    public static <T> T[] asArray(Collection<T> coll, T[] arr) {
        int i = 0;
        for (T o : coll) {
            arr[i++] = o;
        }
        return arr;
    }

    public static File[] asFileArray(Collection<File> coll) {
        return asArray(coll, new File[coll.size()]);
    }

    public static Class<?>[] asClassArray(Collection<Class<?>> coll) {
        return asArray(coll, new Class<?>[coll.size()]);
    }
}
