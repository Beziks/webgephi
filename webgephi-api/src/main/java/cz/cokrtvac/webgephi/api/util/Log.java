package cz.cokrtvac.webgephi.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoggerFactory wrapper
 *
 * @author beziks
 */
public class Log {
    public static Logger get(String name) {
        return LoggerFactory.getLogger(name);
    }

    public static Logger get(Class clazz) {
        return get(clazz.getName());
    }
}
