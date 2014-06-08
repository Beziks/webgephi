package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function;

import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 17. 5. 2014
 * Time: 16:59
 */
public interface GraphFunction {
    public String getId();

    public String getName();

    public String getDescription();

    public GraphFunctionProperty getProperty(String id);

    public List<GraphFunctionProperty> getProperties();

    /**
     * @return Deep copy of this object
     */
    public GraphFunction copy();
}
