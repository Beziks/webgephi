package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.docs;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.oauth_authorization.ScopeWidget;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Page(path = "authDocs")
@Templated("#root")
@ApplicationScoped
public class AuthorizationDocPage extends Composite {
    @Inject
    private Logger log;

    @Inject
    @Alert
    private javax.enterprise.event.Event<String> messageEvent;

    @Inject
    @DataField
    private FlowPanel scopes;

    @Inject
    private Instance<ScopeWidget> scopeWidgets;


    @AfterInitialization
    protected void init() {
        for(Role r : Role.values()){
            ScopeWidget w = scopeWidgets.get();
            w.init(r);
            scopes.add(w);
        }
    }

    @PageShown
    protected void onShow() {
    }
}
