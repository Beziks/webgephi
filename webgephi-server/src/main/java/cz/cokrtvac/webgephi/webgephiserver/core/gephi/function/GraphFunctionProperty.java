package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 17. 5. 2014
 * Time: 16:59
 */
public interface GraphFunctionProperty<T> {
    public String getId();

    public String getName();

    public String getDescription();

    public Class<T> getValueType();

    public T getValue();

    public void setValue(T value);
}
