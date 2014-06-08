package example;

import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;

import java.lang.reflect.InvocationTargetException;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 1. 5. 2014
 * Time: 12:24
 */
public class LookupApiExample {
    public void example() throws InvocationTargetException, IllegalAccessException {
// 20 - 29 ================================================================================
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
// ================================================================================
    }
}
