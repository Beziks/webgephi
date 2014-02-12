package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.account;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 22.1.14
 * Time: 22:51
 */
@Page(path = "createAccount")
@Templated("#root")
public class CreateUserAccountPage extends Composite {
    @Inject
    @DataField
    private UserProfileWidget userProfileWidget;
}
