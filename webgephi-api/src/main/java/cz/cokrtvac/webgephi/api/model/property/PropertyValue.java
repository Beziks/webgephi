package cz.cokrtvac.webgephi.api.model.property;

import javax.xml.bind.annotation.XmlTransient;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12. 5. 2014
 * Time: 22:36
 */
@XmlTransient
public abstract class PropertyValue {
    public abstract String getType();
}
