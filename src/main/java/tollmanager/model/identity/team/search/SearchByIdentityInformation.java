package tollmanager.model.identity.team.search;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.shared.ISearch;

import java.util.*;
import java.util.stream.Collectors;

public class SearchByIdentityInformation  implements ISearch<Employee>{

    /**
     * search with name,forename,niss
     * @param collection where search
     * @param keyword element to search into the collection
     * @return set of employee
     */
    @Override
    public Set<Employee> search(Collection<Employee> collection, String keyword) {
        Set<Employee> employees=new LinkedHashSet<>();
        ArrayList<String> keywords=slideKeyword(keyword);

        if(keywords.isEmpty())
            return (Set<Employee>) collection;

        String word=keywords.remove(0);

        SearchByName byName=new SearchByName();
        SearchByForename byForename=new SearchByForename();
        SearchByNiss byNiss=new SearchByNiss();

        employees.addAll(byName.search(collection,word));
        employees.addAll(byForename.search(collection,word));
        employees.addAll(byNiss.search(collection,word));

        return search(employees, buildKeyword(keywords));
    }

    private ArrayList<String> slideKeyword(String element) {
        ArrayList<String> elements=new ArrayList<>();
        String[] words=element.split(" ");

        if(words[0].isEmpty())
            return elements;

        elements.addAll(Arrays.asList(words));
        return elements;
    }

    private String buildKeyword(ArrayList<String> keywords) {
        StringBuilder keyword= new StringBuilder();
        keywords.forEach(keyword::append);
        return keyword.toString();
    }
}
