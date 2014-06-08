package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.converter;

import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import cz.cokrtvac.webgephi.webgephiserver.core.WebgephiXmlFactory;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunctionProperty;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FilterWrapper;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FiltersPool;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.gephi.data.attributes.api.AttributeColumn;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3. 6. 2014
 * Time: 16:08
 * <p/>
 * attribute-non-null-filter =====================================
 * <p/>
 * <property id="constructor-param-0">
 * <attribute attributeId="modularity_class"/>
 * <p/>
 * <property id="column">
 * <attribute attributeId="modularity_class"/>
 */
public class AttributeNonNullFilterCustomConverter extends FilterCustomConverter.FilterBaseCustomConverter {

    @Override
    public FilterXml convert(FilterWrapper filterWrapper) {
        FilterXml xml = createBase(filterWrapper);

        GraphFunctionProperty<AttributeColumn> pColumn = filterWrapper.getProperty("column");

        PropertyXml<AttributePropertyValue> p = WebgephiXmlFactory.createXml(pColumn, new AttributePropertyValue(pColumn.getId()));
        p.setId("attribute");
        p.setName("Attribute column");
        p.setDescription("Select attribute to filter");
        xml.addProperty(p);
        return xml;
    }

    @Override
    public FilterWrapper convert(FilterXml filterXml, FiltersPool pool, WorkspaceWrapper ww) throws ValidationException {
        FilterWrapper function = pool.createNew(filterXml);
        AttributePropertyValue attribute = (AttributePropertyValue) filterXml.getProperty("attribute").getValue();

        AttributeColumn attributeColumn = FunctionProcessor.toAttributeColumn(attribute, ww);

        function.getProperty("constructor-param-0").setValue(attributeColumn);
        function.getProperty("column").setValue(attributeColumn);
        return function;
    }
}
