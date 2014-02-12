package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 20.1.14
 * Time: 22:44
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Alert {
}
