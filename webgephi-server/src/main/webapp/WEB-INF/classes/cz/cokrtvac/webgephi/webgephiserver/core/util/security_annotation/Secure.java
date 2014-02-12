package cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation;


import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 27.1.14
 * Time: 21:25
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@InterceptorBinding
public @interface Secure {
    /**
     * Required roles. Logged user has to have at least one of these roles.
     */
    @Nonbinding  Role[] value() default Role.ANY;

    /**
     * Username of the owner of the resource (e.g. entity which it is manipulated with).
     * If set, logged users username has to be the same as result of this expression. Otherwise access id denied (except role ADMIN).
     *
     * Can be an expression (like jsp expression). You can use:
     *   <ul>
     *       <li>Method parameters: e.g. #{arg0.username} for method test(User user, String text)</li>
     *       <li>All constructs of el</li>
     *   </ul>
     */
    @Nonbinding  String owner() default "";

    /**
     * Additional test - an expression, which have to return true to allow access.
     * Used only if it is set.

     * You can use:
     *   <ul>
     *       <li>Method parameters: e.g. #{arg0.username} for method test(User user, String text)</li>
     *       <li>All constructs of el. E.g. #{arg1 == 'aaa'} for method test(User user, String text)</li>
     *   </ul>
     */
    @Nonbinding  String condition() default "";
}
