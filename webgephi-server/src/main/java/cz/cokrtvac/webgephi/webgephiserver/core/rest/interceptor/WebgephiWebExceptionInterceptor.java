package cz.cokrtvac.webgephi.webgephiserver.core.rest.interceptor;

import cz.cokrtvac.webgephi.api.model.WebgephiWebException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 30.1.14
 * Time: 20:35
 */
@WebException
@Interceptor
public class WebgephiWebExceptionInterceptor {
    @Inject
    private Logger log;

    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Exception {
        try {
            return ctx.proceed();
        } catch (WebgephiWebException we) {
            throw we;
        } catch (SecurityException se) {
            throw new WebgephiWebException(Response.Status.FORBIDDEN, "You don't have permission to access this resource", se);
        } catch (Exception e) {
            log.error("Unexpected server exception during " + ctx.getMethod().getName() + " method", e);
            throw new WebgephiWebException(Response.Status.INTERNAL_SERVER_ERROR, "Unexpected server exception", e);
        }
    }
}
