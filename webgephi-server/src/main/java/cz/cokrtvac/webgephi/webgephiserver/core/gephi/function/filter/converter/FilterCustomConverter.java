package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.converter;

import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.webgephiserver.core.WebgephiXmlFactory;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunctionProperty;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FilterWrapper;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FiltersPool;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 1. 6. 2014
 * Time: 21:01
 */
public interface FilterCustomConverter {
    public static Logger log = LoggerFactory.getLogger(FilterCustomConverter.class);

    public FilterXml convert(FilterWrapper filterWrapper);

    public FilterWrapper convert(FilterXml filterXml, FiltersPool pool, WorkspaceWrapper ww) throws ValidationException;

    public static class Converters {
        private static final Map<String, FilterCustomConverter> converters = init();
        private static FilterCustomConverter defaultConverter = new FilterDefaultConverter();

        private static Map<String, FilterCustomConverter> init() {
            Map<String, FilterCustomConverter> map = new HashMap<String, FilterCustomConverter>();
            map.put("partition-filter", new PartitionFilterCustomConverter());
            map.put("intra-edges-filter", new PartitionFilterCustomConverter());
            map.put("inter-edges-filter", new PartitionFilterCustomConverter());

            map.put("equal-boolean-filter", new AttributeEqualsFilterCustomCoverters.BooleanEquals());
            map.put("equal-number-filter", new AttributeEqualsFilterCustomCoverters.NumberEquals());
            map.put("equal-string-filter", new AttributeEqualsFilterCustomCoverters.StringEquals());

            map.put("attribute-non-null-filter", new AttributeNonNullFilterCustomConverter());
            map.put("attribute-range-filter", new AttributeRangeFilterCustomConverter());
            map.put("edge-weight", new EdgeWeightFilterCustomConverter());
            map.put("partition-count-filter", new PartitionCountFilterCustomConverter());
            return map;
        }

        public static FilterXml convert(FilterWrapper filterWrapper) {
            if (converters.containsKey(filterWrapper.getId())) {
                return converters.get(filterWrapper.getId()).convert(filterWrapper);
            }
            return defaultConverter.convert(filterWrapper);
        }

        public static FilterWrapper convert(FilterXml filterXml, FiltersPool pool, WorkspaceWrapper ww) throws ValidationException {
            if (converters.containsKey(filterXml.getId())) {
                return converters.get(filterXml.getId()).convert(filterXml, pool, ww);
            }
            return defaultConverter.convert(filterXml, pool, ww);
        }
    }

    // Abstract base ================================================================================
    public abstract static class FilterBaseCustomConverter implements FilterCustomConverter {
        protected FilterXml createBase(FilterWrapper filterWrapper) {
            FilterXml xml = new FilterXml();
            xml.setId(filterWrapper.getId());
            xml.setName(filterWrapper.getName());
            xml.setDescription(filterWrapper.getDescription());
            return xml;
        }
    }

    // Default ================================================================================
    public static class FilterDefaultConverter extends FilterBaseCustomConverter {
        @Override
        public FilterXml convert(FilterWrapper filterWrapper) {
            FilterXml xml = createBase(filterWrapper);
            for (GraphFunctionProperty<?> p : filterWrapper.getProperties()) {
                xml.addProperty(WebgephiXmlFactory.createXml(p));
            }
            return xml;
        }

        @Override
        public FilterWrapper convert(FilterXml filterXml, FiltersPool pool, WorkspaceWrapper ww) {
            FilterWrapper function = pool.createNew(filterXml);
            for (GraphFunctionProperty p : function.getProperties()) {
                PropertyXml<?> pXml = filterXml.getProperty(p.getId());
                if (pXml != null) {
                    try {
                        log.info(p.getName() + " | " + p.getValue() + " | " + pXml.getValue());
                        Object value = FunctionProcessor.toFunctionPropertyValue(pXml.getValue(), ww);
                        p.setValue(value);
                    } catch (Exception e) {
                        log.error("Property cannot be set.", e);
                    }
                }
            }
            return function;
        }
    }
}
