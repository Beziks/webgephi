package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.consumer;

import com.google.gwt.user.client.ui.Composite;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import org.jboss.errai.security.shared.RequireAuthentication;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;

import javax.inject.Inject;

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
