package cz.cokrtvac.webgephi.clientapp.ui.functions;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.*;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.util.XmlFastUtil;
import cz.cokrtvac.webgephi.client.ErrorHttpResponseException;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.Selected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 16. 4. 2014
 * Time: 23:07
 */
@UIScoped
public class UploadGraphWidget extends CustomComponent {
    protected Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private UserSession userSession;

    @Inject
    @Selected
    private javax.enterprise.event.Event<GraphDetailXml> graphSelectedEvent;

    @PostConstruct
    public void init() {
        Label label = new Label("Upload GEXF file or copy paste it into a text area");
        VerticalLayout vl = new VerticalLayout(label);
        setSizeFull();
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setSizeFull();
        setSizeFull();
        setCompositionRoot(vl);

        final UploadReceiver uploadReceiver = new UploadReceiver();

        Upload upload = new Upload(null, uploadReceiver);
        upload.setImmediate(false);
        upload.setButtonCaption("Upload File");
        final UploadInfoWindow uploadInfoWindow = new UploadInfoWindow(upload, uploadReceiver);
        upload.addStartedListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(final Upload.StartedEvent event) {
                if (uploadInfoWindow.getParent() == null) {
                    UI.getCurrent().addWindow(uploadInfoWindow);
                }
                uploadInfoWindow.setClosable(false);
            }
        });

        final TextField nameField = new TextField("Graph name");
        nameField.setWidth(100, Unit.PERCENTAGE);
        nameField.setRequired(true);

        final TextArea textArea = new TextArea();
        textArea.setMaxLength(-1);
        textArea.setSizeFull();
        textArea.setRequired(true);
        textArea.setRequiredError("Value is required. Upload file of copy paste graph in *.gexf format directly.");

        upload.addFinishedListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(final Upload.FinishedEvent event) {
                uploadInfoWindow.setClosable(true);
                try {
                    String s = uploadReceiver.getStream().toString();
                    log.info(s);
                    Document doc = XmlFastUtil.lsDeSerializeDom(uploadReceiver.getStream().toByteArray());
                    String data = XmlFastUtil.lsSerializeDomPretty(doc);
                    log.info("Uploaded :" + data);
                    textArea.setValue(data);
                    nameField.setValue(event.getFilename());
                } catch (Exception e) {
                    log.error("Invalid format " + e.getMessage(), e);
                    Notification.show("Invalid format", e.getMessage(), Notification.Type.ERROR_MESSAGE);
                } finally {
                    uploadReceiver.getStream().reset();
                }
            }
        });

        vl.addComponent(upload);

        //--------------

        vl.addComponent(nameField);
        vl.addComponent(textArea);
        vl.setExpandRatio(textArea, 1);

        vl.addComponent(new Button("Create graph", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if(!nameField.isValid() || !textArea.isValid()){
                    Notification.show("Invalid input data", "Check required fields", Notification.Type.WARNING_MESSAGE);
                    return;
                }

                Document doc = null;
                try {
                    doc = XmlFastUtil.lsDeSerializeDom(textArea.getValue());
                } catch (Exception e) {
                    log.error("Invalid format " + e.getMessage(), e);
                    Notification.show("Input in textarea is not a valid xml.", e.getMessage(), Notification.Type.WARNING_MESSAGE);
                    return;
                }

                try {
                    GraphDetailXml xml = userSession.getWebgephiClient().addGraph(nameField.getValue(), doc);
                    graphSelectedEvent.fire(xml);
                    nameField.setValue("");
                    textArea.setValue("");
                    Notification.show("New graph created.", Notification.Type.TRAY_NOTIFICATION);
                } catch (ErrorHttpResponseException e) {
                    Notification.show("Input is invalid.", e.getMessage(), Notification.Type.WARNING_MESSAGE);
                } catch (WebgephiClientException e) {
                    Notification.show("Communication error.", e.getMessage(), Notification.Type.WARNING_MESSAGE);
                    log.error(e.getMessage(), e);
                }
            }
        }));
    }

    private static class UploadInfoWindow extends Window implements Upload.StartedListener, Upload.ProgressListener, Upload.FailedListener, Upload.SucceededListener, Upload.FinishedListener {
        private final Label state = new Label();
        private final Label fileName = new Label();
        private final Label textualProgress = new Label();

        private final ProgressIndicator pi = new ProgressIndicator();
        private final Button cancelButton;
        private final UploadReceiver uploadReceiver;

        public UploadInfoWindow(final Upload upload, final UploadReceiver uploadReceiver) {
            super("Status");
            this.uploadReceiver = uploadReceiver;

            addStyleName("upload-info");

            setResizable(false);
            setDraggable(false);

            final FormLayout l = new FormLayout();
            setContent(l);
            l.setMargin(true);

            final HorizontalLayout stateLayout = new HorizontalLayout();
            stateLayout.setSpacing(true);
            stateLayout.addComponent(state);

            cancelButton = new Button("Cancel");
            cancelButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final Button.ClickEvent event) {
                    upload.interruptUpload();
                }
            });

            cancelButton.setVisible(false);
            cancelButton.setStyleName("small");
            stateLayout.addComponent(cancelButton);

            stateLayout.setCaption("Current state");
            state.setValue("Idle");
            l.addComponent(stateLayout);

            fileName.setCaption("File name");
            l.addComponent(fileName);

            pi.setCaption("Progress");
            pi.setVisible(false);
            l.addComponent(pi);

            textualProgress.setVisible(false);
            l.addComponent(textualProgress);

            upload.addStartedListener(this);
            upload.addProgressListener(this);
            upload.addFailedListener(this);
            upload.addSucceededListener(this);
            upload.addFinishedListener(this);
        }

        @Override
        public void uploadFinished(final Upload.FinishedEvent event) {
            state.setValue("Done");
            pi.setVisible(false);
            textualProgress.setVisible(false);
            cancelButton.setVisible(false);
        }

        @Override
        public void uploadStarted(final Upload.StartedEvent event) {
            // this method gets called immediatedly after upload is started
            pi.setValue(0f);
            pi.setVisible(true);
            pi.setPollingInterval(500); // hit server frequantly to get
            textualProgress.setVisible(true);
            // updates to client
            state.setValue("Uploading");
            fileName.setValue(event.getFilename());
            cancelButton.setVisible(true);
        }

        @Override
        public void updateProgress(final long readBytes, final long contentLength) {
            // this method gets called several times during the update
            pi.setValue(new Float(readBytes / (float) contentLength));
            textualProgress.setValue("Processed " + readBytes + " bytes of " + contentLength);
        }

        @Override
        public void uploadSucceeded(final Upload.SucceededEvent event) {
            state.setValue("Done");
        }

        @Override
        public void uploadFailed(final Upload.FailedEvent event) {
            state.setValue("Failed");
        }
    }

    private static class UploadReceiver implements Upload.Receiver {
        private ByteArrayOutputStream os = new ByteArrayOutputStream();


        @Override
        public OutputStream receiveUpload(final String filename, final String MIMEType) {
            return os;
        }

        public ByteArrayOutputStream getStream() {
            return os;
        }
    }
}
