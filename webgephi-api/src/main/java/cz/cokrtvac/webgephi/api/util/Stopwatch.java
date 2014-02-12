package cz.cokrtvac.webgephi.api.util;

import org.slf4j.Logger;

public class Stopwatch {
    private static Logger LOG = Log.get(Stopwatch.class);
    long start;

    public Stopwatch() {
        if (LOG.isDebugEnabled()) {
            reset();
        }
    }

    public long time() {
        if (LOG.isDebugEnabled()) {
            return getSystemTime() - start;
        }
        return 0;
    }

    public void reset() {
        if (LOG.isDebugEnabled()) {
            start = getSystemTime();
        }
    }

    public long print() {
        if (LOG.isDebugEnabled()) {
            long time = time();
            LOG.debug("Stopwatch: " + time + "ms");
            return time;
        }
        return 0;
    }

    private long getSystemTime() {
        return System.currentTimeMillis();
    }
}
