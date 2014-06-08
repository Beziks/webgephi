package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.converter;

import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.basic.BooleanPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.NumberPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.StringPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.AttributeBooleanEqualsPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.AttributeNumberEqualsPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.AttributeStringEqualsPropertyValue;
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
 * Time: 15:36
 */
public class AttributeEqualsFilterCustomCoverters {
    /**
     * equal-boolean-filter =============================================================
     * <property id="constructor-param-0"><description>0. parameter of constructor</description>
     * <attribute attributeId="modularity_class"/>
     * <p/>
     * <property id="column">
     * <attribute attributeId="modularity_class"/>
     * <p/>
     * <property id="match">
     * <boolean value="false"/>
     */
    static class BooleanEquals extends FilterCustomConverter.FilterBaseCustomConverter {

        @Override
        public FilterXml convert(FilterWrapper filterWrapper) {
            FilterXml xml = createBase(filterWrapper);

            GraphFunctionProperty<AttributeColumn> pColumn = filterWrapper.getProperty("column");
            GraphFunctionProperty<Boolean> pMatch = filterWrapper.getProperty("match");

            PropertyXml<AttributeBooleanEqualsPropertyValue> p = WebgephiXmlFactory.createXml(pColumn, new AttributeBooleanEqualsPropertyValue(pColumn.getId()));
            p.setId("attribute-value");
            p.setName("Attribute value");
            p.setDescription("Select boolean attribute and value to filter");
            if (pMatch.getValue() != null) {
                p.getValue().setAttributeValue(new BooleanPropertyValue(pMatch.getValue()));
            }

            xml.addProperty(p);
            return xml;
        }

        @Override
        public FilterWrapper convert(FilterXml filterXml, FiltersPool pool, WorkspaceWrapper ww) throws ValidationException {
            FilterWrapper function = pool.createNew(filterXml);
            AttributeBooleanEqualsPropertyValue xmlValue = (AttributeBooleanEqualsPropertyValue) filterXml.getProperties().get(0).getValue();

            AttributeColumn attributeColumn = FunctionProcessor.toAttributeColumn(xmlValue, ww);
            Boolean attributeValue = (Boolean) FunctionProcessor.toFunctionPropertyValue(xmlValue.getAttributeValue(), ww);

            function.getProperty("constructor-param-0").setValue(attributeColumn);
            function.getProperty("column").setValue(attributeColumn);
            function.getProperty("match").setValue(attributeValue);
            return function;
        }
    }

    /**
     * equal-number-filter =============================================================
     * <property id="constructor-param-0"><description>0. parameter of constructor</description>
     * <attribute attributeId="modularity_class"/>
     * <p/>
     * <property id="column">
     * <attribute attributeId="modularity_class"/>
     * <p/>
     * <property id="match">
     * <double/>
     * <p/>
     * <property id="range">
     * <range />
     */
    public static class NumberEquals extends FilterCustomConverter.FilterBaseCustomConverter {

        @Override
        public FilterXml convert(FilterWrapper filterWrapper) {
            FilterXml xml = createBase(filterWrapper);

            GraphFunctionProperty<AttributeColumn> pColumn = filterWrapper.getProperty("column");
            GraphFunctionProperty<Number> pMatch = filterWrapper.getProperty("match");

            PropertyXml<AttributeNumberEqualsPropertyValue> p = WebgephiXmlFactory.createXml(pColumn, new AttributeNumberEqualsPropertyValue(pColumn.getId()));
            p.setId("attribute-value");
            p.setName("Attribute value");
            p.setDescription("Select number attribute and value to filter");
            if (pMatch.getValue() != null) {
                p.getValue().setAttributeValue(NumberPropertyValue.create(pMatch.getValue()));
            }
            xml.addProperty(p);
            return xml;
        }

        @Override
        public FilterWrapper convert(FilterXml filterXml, FiltersPool pool, WorkspaceWrapper ww) throws ValidationException {
            FilterWrapper function = pool.createNew(filterXml);
            AttributeNumberEqualsPropertyValue xmlValue = (AttributeNumberEqualsPropertyValue) filterXml.getProperties().get(0).getValue();

            AttributeColumn attributeColumn = FunctionProcessor.toAttributeColumn(xmlValue, ww);
            Number attributeValue = (Number) FunctionProcessor.toFunctionPropertyValue(xmlValue.getAttributeValue(), ww);

            function.getProperty("constructor-param-0").setValue(attributeColumn);
            function.getProperty("column").setValue(attributeColumn);
            function.getProperty("match").setValue(attributeValue);
            // Good for nothing
            function.getProperty("range").setValue(new Range(attributeValue, attributeValue));
            return function;
        }
    }

    /**
     * equal-string-filter =============================================================
     * <property id="constructor-param-0"><description>0. parameter of constructor</description>
     * <attribute attributeId="modularity_class"/>
     * <p/>
     * <property id="column">
     * <attribute attributeId="modularity_class"/>
     * <p/>
     * <property id="pattern">
     * <string/>
     * <p/>
     * <property id="useregex">
     * <boolean value="false"/>
     */
    public static class StringEquals extends FilterCustomConverter.FilterBaseCustomConverter {

        @Override
        public FilterXml convert(FilterWrapper filterWrapper) {
            FilterXml xml = createBase(filterWrapper);

            GraphFunctionProperty<AttributeColumn> pColumn = filterWrapper.getProperty("column");
            GraphFunctionProperty<String> pPattern = filterWrapper.getProperty("pattern");
            GraphFunctionProperty<Boolean> pUseRegex = filterWrapper.getProperty("useregex");

            PropertyXml<AttributeStringEqualsPropertyValue> p = WebgephiXmlFactory.createXml(pColumn, new AttributeStringEqualsPropertyValue(pColumn.getId()));
            p.setId("attribute-value");
            p.setName("Attribute value");
            p.setDescription("Select string attribute and value to filter");
            if (pPattern.getValue() != null) {
                p.getValue().setAttributeValue(new StringPropertyValue(pPattern.getValue()));
            }
            xml.addProperty(p);

            xml.addProperty(WebgephiXmlFactory.createXml(pUseRegex));
            return xml;
        }

        @Override
        public FilterWrapper convert(FilterXml filterXml, FiltersPool pool, WorkspaceWrapper ww) throws ValidationException {
            FilterWrapper function = pool.createNew(filterXml);
            AttributeStringEqualsPropertyValue xmlValue = (AttributeStringEqualsPropertyValue) filterXml.getProperty("attribute-value").getValue();

            AttributeColumn attributeColumn = FunctionProcessor.toAttributeColumn(xmlValue, ww);
            String attributeValue = (String) FunctionProcessor.toFunctionPropertyValue(xmlValue.getAttributeValue(), ww);
            Boolean useregex = (Boolean) FunctionProcessor.toFunctionPropertyValue(filterXml.getProperty("useregex").getValue(), ww);

            function.getProperty("constructor-param-0").setValue(attributeColumn);
            function.getProperty("column").setValue(attributeColumn);
            function.getProperty("pattern").setValue(attributeValue);
            function.getProperty("useregex").setValue(useregex);

            return function;
        }
    }
}
