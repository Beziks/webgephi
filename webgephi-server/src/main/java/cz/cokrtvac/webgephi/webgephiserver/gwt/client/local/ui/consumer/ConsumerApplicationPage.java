package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.consumer;

import com.google.gwt.user.client.ui.Composite;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.account.AuthorizedConsumersWidget;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.account.UserProfileWidget;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.UserService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.User;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.VoidCallback;
import org.jboss.errai.security.shared.RequireAuthentication;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.validation.Validator;

@Page(path = "consumerApplication")
@Templated("#root")
@RequireAuthentication
public class ConsumerApplicationPage extends Composite {
    @Inject
    Logger log;

    @Inject
    @Alert
    private javax.enterprise.event.Event<String> messageEvent;

    @Inject
    @DataField
    private ConsumerApplicationWidget consumerApplicationWidget;


}
