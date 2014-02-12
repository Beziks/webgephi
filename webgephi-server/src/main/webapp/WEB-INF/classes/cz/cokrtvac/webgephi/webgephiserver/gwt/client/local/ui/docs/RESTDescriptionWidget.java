package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.docs;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.docs.rest.RestDescription;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12.2.14
 * Time: 14:00
 */
@Templated("RESTDescriptionDocPage.html#rootWidget")
public class RESTDescriptionWidget extends Composite {
    @Inject
    @AutoBound
    public DataBinder<RestDescription> restDescription;

    @Bound
    @DataField
    private Element name = DOM.createElement("h3");

    @Bound
    @DataField
    private Element description = DOM.createDiv();

    @Bound
    @DataField
    private Element method = DOM.createSpan();

    @Bound
    @DataField
    private Element resource = DOM.createSpan();

    @Bound
    @DataField
    private Element authentication = DOM.createSpan();

    @Bound
    @DataField
    private Element sampleRequest = DOM.createDiv();

    @Bound
    @DataField
    private Element sampleRequestBody = DOM.createElement("code");

    @Bound
    @DataField
    private Element sampleResponseBody = DOM.createElement("code");


    @DataField
    private Element requestToogle  = DOM.createAnchor();

    @DataField
    private Element request  = DOM.createDiv();

    @DataField
    private Element responseToogle  = DOM.createAnchor();

    @DataField
    private Element response  = DOM.createDiv();

    @PostConstruct
    protected void init(){
        String requestId = DOM.createUniqueId();
        requestToogle.setAttribute("href", "#" + requestId);
        request.setId(requestId);

        String responseId = DOM.createUniqueId();
        responseToogle.setAttribute("href", "#" + responseId);
        response.setId(responseId);
    }

    public void setModel(RestDescription d){
        this.restDescription.setModel(d);
    }
}
