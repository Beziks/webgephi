package cz.cokrtvac.webgephi.webgephiserver.core.util;

import cz.cokrtvac.webgephi.api.util.Log;
import cz.cokrtvac.webgephi.webgephiserver.core.InitializationException;
import org.picketlink.idm.IdentityManager;
import org.slf4j.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Iterator;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 24.1.14
 * Time: 13:38
 * <p/>
 * Util class to 'Inject' cdi bean to nonCDI beans (static methods and so on)
 */
public class StaticBeanFactory {
    private static final Logger log = Log.get(StaticBeanFactory.class);

    private static BeanManager getBeanManager() {
        try {
            return (BeanManager) InitialContext.doLookup("java:comp/BeanManager");
        } catch (final NamingException e) {
            log.error("Lookup for BeanManager failed", e);
            throw new InitializationException("Lookup for BeanManager failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T lookup(final Class<T> clazz) {
        final BeanManager bm = getBeanManager();
        final Iterator<Bean<?>> iter = bm.getBeans(clazz).iterator();
        if (!iter.hasNext()) {
            throw new IllegalStateException("UNSATISFIED DEPENDENCY. CDI BeanManager cannot find an instance of requested type " + clazz.getName());
        }
        final Bean<T> bean = (Bean<T>) iter.next();
        if (iter.hasNext()) {
            throw new IllegalStateException("AMBIGUOUS DEPENDENCY. CDI BeanManager cannot found more than one instance of requested type " + clazz.getName());
        }
        final CreationalContext<T> ctx = bm.createCreationalContext(bean);
        return (T) bm.getReference(bean, clazz, ctx);
    }

    public static IdentityManager getIdentityManager() {
        return lookup(IdentityManager.class);
    }
}
