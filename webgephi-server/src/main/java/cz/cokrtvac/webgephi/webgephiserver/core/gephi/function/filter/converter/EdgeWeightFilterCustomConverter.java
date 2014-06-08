package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.converter;

import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FilterWrapper;
import org.gephi.filters.api.Range;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3. 6. 2014
 * Time: 15:34
 * <p/>
 * eddge-weight =============================================================
 * Set range to float...
 */
class EdgeWeightFilterCustomConverter extends FilterCustomConverter.FilterDefaultConverter {
    @Override
    public FilterXml convert(FilterWrapper filterWrapper) {
        Range p = (Range) filterWrapper.getProperties().get(0).getValue();
        if (p == null) {
            p = new Range(0f, 1f);
            filterWrapper.getProperties().get(0).setValue(p);
        }
        return super.convert(filterWrapper);
    }
}
