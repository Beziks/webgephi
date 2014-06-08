package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.converter;

import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.range.AttributeRangePropertyValue;
import cz.cokrtvac.webgephi.webgephiserver.core.WebgephiXmlFactory;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunctionProperty;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FilterWrapper;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FiltersPool;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.filters.api.Range;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3. 6. 2014
 * Time: 15:31
 * <p/>
 * <p/>
 * attribute-range-filter =============================================================
 * <property id="constructor-param-0">
 * <attribute attributeId="modularity_class"/>
 * <p/>
 * <property id="column">
 * <attribute attributeId="modularity_class"/>
 * <p/>
 * <property id="range">
 * <range>
 * <from xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="integerPropertyValue">0</from>
 * <to xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="integerPropertyValue">1</to>
 * </range>
 */
class AttributeRangeFilterCustomConverter extends FilterCustomConverter.FilterBaseCustomConverter {

    @Override
    public FilterXml convert(FilterWrapper filterWrapper) {
        FilterXml xml = createBase(filterWrapper);

        GraphFunctionProperty<AttributeColumn> fp = filterWrapper.getProperty("constructor-param-0");
        PropertyXml<AttributeRangePropertyValue> p = WebgephiXmlFactory.createXml(fp, new AttributeRangePropertyValue(fp.getValue().getId()));
        p.setId("selected-partitions");
        p.setName("Attribute range");
        p.setDescription("Select range of attribute values");

        xml.addProperty(p);
        return xml;
    }

    @Override
    public FilterWrapper convert(FilterXml filterXml, FiltersPool pool, WorkspaceWrapper ww) throws ValidationException {
        FilterWrapper function = pool.createNew(filterXml);
        AttributeRangePropertyValue xmlValue = (AttributeRangePropertyValue) filterXml.getProperties().get(0).getValue();

        AttributeColumn attributeColumn = FunctionProcessor.toAttributeColumn(xmlValue, ww);
        Range range = FunctionProcessor.toRange(xmlValue.getRange(), ww);

        function.getProperty("constructor-param-0").setValue(attributeColumn);
        function.getProperty("column").setValue(attributeColumn);
        function.getProperty("range").setValue(range);
        return function;
    }
}
