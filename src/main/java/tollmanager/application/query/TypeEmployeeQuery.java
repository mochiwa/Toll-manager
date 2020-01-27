package tollmanager.application.query;

import tollmanager.model.access.Group;

import java.util.LinkedHashSet;
import java.util.Set;
/**
 * ask the business what type of employee it has
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class TypeEmployeeQuery {
    private Set<Group> types;

    public TypeEmployeeQuery() {
        types=new LinkedHashSet<Group>();
    }

    public TypeEmployeeQuery(Set<Group> types) {
        this.types = types;
    }

    public Set<Group> getTypes() {
        return types;
    }

    public void setTypes(Set<Group> types) {
        this.types = types;
    }
}
