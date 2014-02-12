package cz.cokrtvac.webgephi.webgephiserver.core.rest.interceptor;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 30.1.14
 * Time: 20:37
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@InterceptorBinding
public @interface WebException {
}
