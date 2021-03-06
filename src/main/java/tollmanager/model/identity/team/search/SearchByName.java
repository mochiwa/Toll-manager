package tollmanager.model.identity.team.search;

import tollmanager.model.identity.Employee;
import tollmanager.model.shared.ISearch;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchByName implements ISearch<Employee> {
    /**
     * search with the name of a person
     * @param collection where search
     * @param element element to search into the collection
     * @return set of employee
     */
    @Override
    public Set<Employee> search(Collection<Employee> collection, String element) {
       return collection.stream()
                .filter(employee -> employee.person().fullName().name().contains(element))
                .collect(Collectors.toSet());
    }
}
