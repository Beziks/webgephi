package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.oauth_authorization;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;

import javax.inject.Inject;

@Templated("AuthorizeConsumerPage.html#rootScope")
public class ScopeWidget extends Composite {
    @Inject
    private Logger log;

    public void init(Role role) {
        scopeTitle.setInnerText(role.getTitle() + " (" + role.name() + ")");
        scopeDescription.setInnerText(role.description());
    }

    @DataField
    private Element scopeTitle = DOM.createElement("h4");

    @DataField
    private Element scopeDescription = DOM.createElement("p");


}
