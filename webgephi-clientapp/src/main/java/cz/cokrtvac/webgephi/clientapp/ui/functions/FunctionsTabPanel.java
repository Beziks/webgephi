package cz.cokrtvac.webgephi.clientapp.ui.functions;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.filter.FiltersXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutsXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingsXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticsXml;
import cz.cokrtvac.webgephi.client.ErrorHttpResponseException;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.Selected;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 18:12
 */
@UIScoped
public class FunctionsTabPanel extends CustomComponent {
    private TabSheet tabSheet;

    private List<FunctionSettingWidget> allFunctions = new ArrayList<FunctionSettingWidget>();

    @Inject
    private Logger log;

    @Inject
    private UserSession userSession;

    @Inject
    private UploadGraphWidget uploadGraphWidget;

    private GraphDetailXml currentGraph;

    public FunctionsTabPanel() {
        tabSheet = new TabSheet();
        tabSheet.setHeight(100.0f, Unit.PERCENTAGE);
        setCompositionRoot(tabSheet);
    }

    @PostConstruct
    private void init() throws WebgephiClientException, ErrorHttpResponseException {
        // Upload
        tabSheet.addTab(uploadGraphWidget, "Create new graph");

        // Layouts
        LayoutsXml layoutsXml = userSession.getWebgephiClient().getLayouts();

        Accordion layAccordion = new Accordion();
        tabSheet.addTab(layAccordion, "Layouts");
        layAccordion.setHeight(100.0f, Unit.PERCENTAGE);

        for (LayoutXml l : layoutsXml.getFunctions()) {
            FunctionSettingWidget s = new LayoutSettingWidget(l, userSession);
            TabSheet.Tab tab = layAccordion.addTab(s, l.getName());
            allFunctions.add(s);
        }

        // Statistics
        StatisticsXml statisticsXml = userSession.getWebgephiClient().getStatistics();

        Accordion statAccordion = new Accordion();
        tabSheet.addTab(statAccordion, "Statistics");
        statAccordion.setHeight(100.0f, Unit.PERCENTAGE);

        for (StatisticXml st : statisticsXml.getFunctions()) {
            FunctionSettingWidget s = new StatisticSettingWidget(st, userSession);
            TabSheet.Tab tab = statAccordion.addTab(s, st.getName());
            allFunctions.add(s);
        }

        // Rankings
        RankingsXml rankingsXml = userSession.getWebgephiClient().getRankings();

        Accordion rankingsAccordion = new Accordion();
        tabSheet.addTab(rankingsAccordion, "Rankings");
        rankingsAccordion.setHeight(100.0f, Unit.PERCENTAGE);

        for (RankingXml rank : rankingsXml.getFunctions()) {
            RankingSettingWidget s = new RankingSettingWidget(rank, userSession);
            TabSheet.Tab tab = rankingsAccordion.addTab(s, rank.getName());
            allFunctions.add(s);
        }

        // Filters
        FiltersXml filtersXml = userSession.getWebgephiClient().getFilters();

        Accordion filtersAccordion = new Accordion();
        tabSheet.addTab(filtersAccordion, "Filters");
        filtersAccordion.setHeight(100.0f, Unit.PERCENTAGE);

        for (FilterXml filt : filtersXml.getFunctions()) {
            RankingSettingWidget s = new RankingSettingWidget(filt, userSession);
            TabSheet.Tab tab = filtersAccordion.addTab(s, filt.getName());
            allFunctions.add(s);
        }
    }

    private void updateSelection(@Observes @Selected GraphDetailXml graph) {
        if (currentGraph != null && graph.getId().equals(currentGraph.getId())) {
            // No update needed
            return;
        }
        currentGraph = graph;
        for (FunctionSettingWidget w : allFunctions) {
            w.graphChanged(currentGraph, userSession);
        }
    }


}
