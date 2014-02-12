package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.docs;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.DocsService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.docs.rest.RestDescription;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;

@Page(path = "restDocs")
@Templated("#root")
@ApplicationScoped
public class RESTDescriptionDocPage extends Composite {
    @Inject
    private Logger log;

    @Inject
    @Alert
    private javax.enterprise.event.Event<String> messageEvent;

    @Inject
    private Caller<DocsService> docsServiceCaller;

    @Inject
    @DataField
    private FlowPanel panel;

    @Inject
    private Instance<RESTDescriptionWidget> restDescriptionWidgets;


    @AfterInitialization
    protected void init() {
        // messageEvent.fire("afterInitialization");
        docsServiceCaller.call(
                new RemoteCallback<List<RestDescription>>() {
                    @Override
                    public void callback(List<RestDescription> o) {
                        for (RestDescription d : o) {
                            RESTDescriptionWidget w = restDescriptionWidgets.get();
                            w.setModel(d);
                            panel.add(w);
                        }
                    }
                },
                new ErrorCallback<Object>() {
                    @Override
                    public boolean error(Object o, Throwable throwable) {
                        messageEvent.fire("Error: " + throwable.getMessage());
                        return false;
                    }
                }
        ).getRestDescriptions();
    }

    @PageShown
    protected void onShow() {
        highlightSyntax();
    }

    /*@Inject
    @DataField
    private Button highlightButton; */

    /*@EventHandler("highlightButton")
    private void highlightButtonClicked(ClickEvent event) {
        highlightSyntax();
    }  */

    public static native void highlightSyntax() /*-{
        $wnd.hljs.initHighlighting();
    }-*/;
}
