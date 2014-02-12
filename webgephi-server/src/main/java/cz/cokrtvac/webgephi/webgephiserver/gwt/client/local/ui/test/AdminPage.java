package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.test;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.security.shared.RequireRoles;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Page(path = "adminTest")
@Templated("#root")
@RequireRoles("admin")
public class AdminPage extends Composite {

}
