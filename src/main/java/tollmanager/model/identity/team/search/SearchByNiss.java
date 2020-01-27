package tollmanager.model.identity.team.search;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.shared.ISearch;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchByNiss implements ISearch<Employee> {

    /**
     * Search with the NISS of a person
     * @param collection where search
     * @param element element to search into the collection
     * @return set of employee
     */
    @Override
    public Set<Employee> search(Collection<Employee> collection, String element) {
        return collection.stream()
                .filter(employee -> {
                    if(element.trim().length()>=2)
                        try{
                            return employee.niss().equals(Niss.of(element));
                        }catch (Exception e){
                            return employee.niss().value().contains(element);
                        }
                        return false;
                })
                .collect(Collectors.toSet());
    }
}
