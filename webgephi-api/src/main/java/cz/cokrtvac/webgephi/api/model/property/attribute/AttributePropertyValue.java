package cz.cokrtvac.webgephi.api.model.property.attribute;

import cz.cokrtvac.webgephi.api.model.property.ComplexPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.ListPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.BasicPropertyValue;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12. 5. 2014
 * Time: 22:38
 */
@XmlAccessorType(XmlAccessType.NONE)
public class AttributePropertyValue extends ComplexPropertyValue {
    public static final String TYPE = "attribute";

    private String attributeId = "id-of-attribute-column";

    public AttributePropertyValue() {
    }

    public AttributePropertyValue(String attributeId) {
        this.setAttributeId(attributeId);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @XmlAttribute(required = true)
    public final String getAttributeId() {
        return attributeId;
    }

    public final void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public List<Attribute> getPossibleAttributes(Document gexfGraph) throws TransformerException {
        return getAllAttributes(gexfGraph);
    }

    protected List<Attribute> filter(List<Attribute> attrs, Filter filter) throws TransformerException {
        List<Attribute> out = new ArrayList<Attribute>();
        for (Attribute a : attrs) {
            if (filter.filter(a)) {
                out.add(a);
            }
        }
        return out;
    }

    private List<Attribute> getAllAttributes(Document gexfGraph) throws TransformerException {
        List<Attribute> out = new ArrayList<Attribute>();
        try {
            NodeList nl = XPathAPI.selectNodeList(gexfGraph.getDocumentElement(), "/gexf/graph/attributes[@class='node']/attribute");
            if (nl != null) {
                for (int i = 0; i < nl.getLength(); i++) {
                    out.add(new Attribute(AttributeType.NODE, (Element) nl.item(i)));
                }
            }

            nl = XPathAPI.selectNodeList(gexfGraph.getDocumentElement(), "/gexf/graph/attributes[@class='edge']/attribute");
            if (nl != null) {
                for (int i = 0; i < nl.getLength(); i++) {
                    out.add(new Attribute(AttributeType.EDGE, (Element) nl.item(i)));
                }
            }
            return out;
        } catch (TransformerException e) {
            log.error("Searching for attributes failed");
            throw e;
        }
    }

    public ListPropertyValue getAllAttributeValues(Document gexfGraph) throws TransformerException {
        ListPropertyValue list = new ListPropertyValue();
        if (list == null) {
            return null;
        }

        Attribute attr = getAttribute(gexfGraph);

        NodeList nl = XPathAPI.selectNodeList(gexfGraph.getDocumentElement(), "/gexf/graph//attvalues/attvalue[@for='" + getAttributeId() + "']");
        if (nl != null) {
            for (int i = 0; i < nl.getLength(); i++) {
                BasicPropertyValue v = BasicPropertyValue.createBasicPropertyValue(attr, ((Element) nl.item(i)).getAttribute("value"));
                if (!list.getValues().contains(v)) {
                    list.getValues().add(v);
                }
            }
        }

        Collections.sort(list.getValues());
        return list;
    }

    protected Attribute getAttribute(Document gexfGraph) throws TransformerException {
        if (getAttributeId() == null) {
            return null;
        }
        for (Attribute a : getPossibleAttributes(gexfGraph)) {
            if (a.getId().equals(getAttributeId())) {
                return a;
            }
        }
        return null;
    }

    public static class Attribute {
        private AttributeType attributeType;
        private String id;
        private String title;
        private String valueType;

        public Attribute(AttributeType attributeType, Element element) {
            this.attributeType = attributeType;
            this.id = element.getAttribute("id");
            this.title = element.getAttribute("title");
            this.valueType = element.getAttribute("type");
        }

        public Attribute(AttributeType attributeType, String id, String title, String valueType) {
            this.attributeType = attributeType;
            this.id = id;
            this.title = title;
            this.valueType = valueType;
        }

        public AttributeType getAttributeType() {
            return attributeType;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getValueType() {
            return valueType;
        }
    }

    public static enum AttributeType {
        NODE,
        EDGE
    }

    public static interface Filter {
        public boolean filter(Attribute attribute);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributePropertyValue that = (AttributePropertyValue) o;

        if (attributeId != null ? !attributeId.equals(that.attributeId) : that.attributeId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return attributeId != null ? attributeId.hashCode() : 0;
    }
}
