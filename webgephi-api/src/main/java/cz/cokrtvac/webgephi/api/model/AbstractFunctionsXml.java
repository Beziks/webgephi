package cz.cokrtvac.webgephi.api.model;

import cz.cokrtvac.webgephi.api.util.CollectionsUtil;
import cz.cokrtvac.webgephi.api.util.Log;
import org.slf4j.Logger;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

@XmlTransient
public abstract class AbstractFunctionsXml<T extends AbstractFunctionXml> {
    protected Logger log = Log.get(getClass());
    private List<T> functions;

    public List<T> getFunctions() {
        if (functions == null) {
            functions = new ArrayList<T>();
        }
        return functions;
    }

    public void setFunctions(List<T> functions) {
        this.functions = functions;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + CollectionsUtil.toString(getFunctions());
    }
}
