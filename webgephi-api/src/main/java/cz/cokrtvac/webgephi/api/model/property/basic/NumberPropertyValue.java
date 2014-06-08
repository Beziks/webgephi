package cz.cokrtvac.webgephi.api.model.property.basic;

import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 2. 6. 2014
 * Time: 17:05
 */
@XmlSeeAlso({
        FloatPropertyValue.class,
        DoublePropertyValue.class,
        IntegerPropertyValue.class
})
public abstract class NumberPropertyValue extends BasicPropertyValue {
    public static final Set<String> NUMBER_TYPES = new HashSet<String>(Arrays.asList(new String[]{"float", "double", "int", "integer", "short", "long"}));

    @Override
    public abstract Number getValue();

    public static NumberPropertyValue create(Number number) {
        if (number instanceof Integer) {
            return new IntegerPropertyValue((Integer) number);
        }
        if (number instanceof Float) {
            return new FloatPropertyValue((Float) number);
        }
        if (number instanceof Double) {
            return new DoublePropertyValue((Double) number);
        }
        throw new IllegalArgumentException("No Property value for " + number + "/" + number.getClass());
    }
}
