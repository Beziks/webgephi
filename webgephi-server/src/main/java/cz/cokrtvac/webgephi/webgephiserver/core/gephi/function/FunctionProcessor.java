package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function;

import cz.cokrtvac.webgephi.api.model.AbstractFunctionXml;
import cz.cokrtvac.webgephi.api.model.property.ColorPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.PropertyValue;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.BasicPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.partition.PartitionPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.partition.PartitionSelectPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.RangePropertyValue;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.filters.api.Range;
import org.gephi.partition.api.Part;
import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 17. 5. 2014
 * Time: 17:27
 */
public abstract class FunctionProcessor<F extends GraphFunction, X extends AbstractFunctionXml> {
    private Logger log = LoggerFactory.getLogger(getClass());

    protected abstract F process(WorkspaceWrapper workspaceWrapper, F function, X xml, Integer repeat);

    protected abstract F createNew(X xml);

    public F process(WorkspaceWrapper workspaceWrapper, X xml, Integer repeat) {
        F function = createNew(xml);
        applySetting(function, xml, workspaceWrapper);
        return process(workspaceWrapper, function, xml, repeat);
    }

    protected final void applySetting(F function, X xml, WorkspaceWrapper workspaceWrapper) {
        for (GraphFunctionProperty p : function.getProperties()) {
            PropertyXml<?> pXml = xml.getProperty(p.getId());
            if (pXml != null) {
                try {
                    log.info(p.getName() + " | " + p.getValue() + " | " + pXml.getValue());

                    Object value = toFunctionPropertyValue(pXml.getValue(), workspaceWrapper);
                    p.setValue(value);
                } catch (Exception e) {
                    log.error("Property cannot be set.", e);
                }
            }
        }
    }

    public static Object toFunctionPropertyValue(PropertyValue xmlValue, WorkspaceWrapper ww) throws ValidationException {
        if (xmlValue instanceof BasicPropertyValue) {
            return ((BasicPropertyValue) xmlValue).getValue();
        }

        if (xmlValue instanceof ColorPropertyValue) {
            return new Color(Integer.valueOf(((ColorPropertyValue) xmlValue).getValue(), 16));
        }

        if (xmlValue instanceof PartitionSelectPropertyValue) {
            return toParts((PartitionSelectPropertyValue) xmlValue, ww);
        }

        if (xmlValue instanceof PartitionPropertyValue) {
            return toPartition((PartitionPropertyValue) xmlValue, ww);
        }

        if (xmlValue instanceof AttributePropertyValue) {
            return toAttributeColumn((AttributePropertyValue) xmlValue, ww);
        }

        if (xmlValue instanceof RangePropertyValue) {
            return toRange((RangePropertyValue) xmlValue, ww);
        }
        throw new IllegalArgumentException("No converter for value type: " + xmlValue.getClass().getSimpleName());
    }

    public static AttributeColumn toAttributeColumn(AttributePropertyValue xmlValue, WorkspaceWrapper ww) throws ValidationException {
        AttributeColumn value = ww.getAttribute((xmlValue).getAttributeId());
        if (value == null) {
            throw new ValidationException("No such attribute exists: " + ((AttributePropertyValue) value).getAttributeId());
        }
        return value;
    }

    public static Partition toPartition(PartitionPropertyValue xmlValue, WorkspaceWrapper ww) throws ValidationException {
        AttributeColumn attr = ww.getAttribute(((AttributePropertyValue) xmlValue).getAttributeId());
        if (attr == null) {
            throw new ValidationException("No such attribute exists: " + ((AttributePropertyValue) attr).getAttributeId());
        }
        PartitionController pc = Lookup.getDefault().lookup(PartitionController.class);
        Partition p = pc.buildPartition(attr, ww.getGraphModel().getGraph());
        return p;
    }

    public static List<Part> toParts(PartitionSelectPropertyValue xmlValue, WorkspaceWrapper ww) throws ValidationException {
        Partition partition = toPartition((PartitionPropertyValue) xmlValue, ww);
        List<Part> parts = new ArrayList<Part>();
        for (BasicPropertyValue selected : ((PartitionSelectPropertyValue) xmlValue).getPartitionValues().getValues()) {
            Part p = partition.getPartFromValue(selected.getValue());
            parts.add(p);
        }
        return parts;
    }

    public static Range toRange(RangePropertyValue xmlValue, WorkspaceWrapper ww) {
        Range r = new Range(xmlValue.getFrom().getValue(), xmlValue.getTo().getValue());
        return r;
    }
}
