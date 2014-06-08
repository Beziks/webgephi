package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.converter;

import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.partition.PartitionPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.RangePropertyValue;
import cz.cokrtvac.webgephi.webgephiserver.core.WebgephiXmlFactory;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunctionProperty;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FilterWrapper;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FiltersPool;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.gephi.partition.api.Partition;

/**
 * partition-count-filter ================================
 * <p/>
 * <property id="constructor-param-0">
 * <attribute attributeId="modularity_class"/>
 * <p/>
 * <property id="constructor-param-1">
 * <nodePartition attributeId="modularity_class"/>
 * <p/>
 * <property id="column">
 * <attribute attributeId="modularity_class"/>
 * <p/>
 * <property id="range">
 * <range><from xsi:type="integerPropertyValue" value="0"/><to xsi:type="integerPropertyValue" value="1"/></range>
 */
class PartitionCountFilterCustomConverter extends FilterCustomConverter.FilterBaseCustomConverter {

    @Override
    public FilterXml convert(FilterWrapper filterWrapper) {
        FilterXml xml = super.createBase(filterWrapper);
        GraphFunctionProperty<Partition> partition = filterWrapper.getProperty("constructor-param-1");
        PropertyXml<PartitionPropertyValue> partitionXml = WebgephiXmlFactory.createXml(partition, new PartitionPropertyValue(partition.getValue().getColumn().getId()));
        PropertyXml<RangePropertyValue> rangeXml = WebgephiXmlFactory.createXml(filterWrapper.getProperty("range"));

        partitionXml.setId("partition");
        partitionXml.setName("Partition");
        partitionXml.setDescription("Select partition");

        rangeXml.setDescription("Select elements number range");

        xml.addProperty(partitionXml);
        xml.addProperty(rangeXml);
        return xml;
    }

    @Override
    public FilterWrapper convert(FilterXml filterXml, FiltersPool pool, WorkspaceWrapper ww) throws ValidationException {
        FilterWrapper function = pool.createNew(filterXml);
        PartitionPropertyValue partitionXmlValue = (PartitionPropertyValue) filterXml.getProperty("partition").getValue();
        RangePropertyValue rangeXmlValue = (RangePropertyValue) filterXml.getProperty("range").getValue();

        Partition partition = FunctionProcessor.toPartition(partitionXmlValue, ww);
        function.getProperty("constructor-param-0").setValue(partition.getColumn());
        function.getProperty("constructor-param-1").setValue(partition);
        function.getProperty("column").setValue(partition.getColumn());


        function.getProperty("range").setValue(FunctionProcessor.toRange(rangeXmlValue, ww));
        return function;
    }
}
