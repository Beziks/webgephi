package cz.cokrtvac.webgephi.clientapp.ui;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.VerticalLayout;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionsTabPanel;
import cz.cokrtvac.webgephi.clientapp.ui.graph_detail.GraphDetailTabPanel;
import cz.cokrtvac.webgephi.clientapp.ui.graph_list.GraphListTabPanel;
import cz.cokrtvac.webgephi.clientapp.ui.template.CentralWidget;
import org.slf4j.Logger;

import javax.inject.Inject;

@CDIView
@SuppressWarnings("serial")
public class MainView extends VerticalLayout implements View {
    @Inject
    private Logger log;

    private CentralWidget centralWidget;

    @Inject
    private FunctionsTabPanel functionsTabPanel;

    @Inject
    private GraphListTabPanel graphListTabPanel;

    @Inject
    private GraphDetailTabPanel graphDetailTabPanel;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        centralWidget = new CentralWidget();
        centralWidget.setLeft(functionsTabPanel);
        centralWidget.setRight(graphListTabPanel);
        centralWidget.setCenter(graphDetailTabPanel);

        Page.getCurrent().addBrowserWindowResizeListener(centralWidget);

        addComponent(centralWidget);
    }
}
