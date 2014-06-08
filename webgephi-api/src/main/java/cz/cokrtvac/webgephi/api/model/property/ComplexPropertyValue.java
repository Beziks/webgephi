package cz.cokrtvac.webgephi.api.model.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlTransient;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12. 5. 2014
 * Time: 22:36
 */
@XmlTransient
public abstract class ComplexPropertyValue extends PropertyValue {
    protected Logger log = LoggerFactory.getLogger(getClass());
}
