package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.auth;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.security.shared.SecurityError;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * @author edewit@redhat.com
 */
@Page(role = SecurityError.class)
@Templated
public class SecurityErrorPage extends Composite {
}
