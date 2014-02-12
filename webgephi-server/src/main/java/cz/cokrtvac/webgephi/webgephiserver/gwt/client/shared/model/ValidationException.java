package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 21.1.14
 * Time: 15:39
 */
@Portable
public class ValidationException extends Exception {
    private List<String> reasons;

    public ValidationException() {
    }

    public ValidationException(String message, String... reasons) {
        this(message, (reasons != null) ? Arrays.asList(reasons) : new ArrayList<String>());
    }

    public ValidationException(String message, List<String> reasons) {
        super(message);
        this.reasons = reasons;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
