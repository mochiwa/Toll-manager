package features.helper.transformer.identity;

import features.helper.transformer.OwnTransformer;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.contact.*;
import tollmanager.model.identity.person.Birthday;
import tollmanager.model.identity.person.FullName;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.person.Person;
import io.cucumber.datatable.TableEntryTransformer;

import java.util.*;

public class EmployeeTransformer implements TableEntryTransformer<Employee> , OwnTransformer {
    private final String ADDRESS_SEPARATOR=";";

    public static EmployeeTransformer of()
    {
        return new EmployeeTransformer();
    }



    @Override
    public Employee transform(Map<String, String> map) throws Throwable {

        return Employee.of(
                EmployeeId.of(map.get("employeeId")),
                Person.of(
                        Niss.of(map.get("niss")),
                        FullName.of(map.get("name"),map.get("forename")),
                        Birthday.of(map.get("birthday")),
                        ContactInformationBuilder.of()
                                .setAddresses(buildAddresses(map.get("address")))
                                .setPhones(buildPhones(map.get("mobile")))
                                .setEmails(buildEmails(map.get("email")))
                                .create()
                )
        );
    }

    private Set<PostalAddress> buildAddresses(String input)
    {
        Set<PostalAddress> addresses=new LinkedHashSet<>();
        separate(input,ADDRESS_SEPARATOR).forEach(address->{
            String[] addressTab=address.split(LIST_SEPARATOR);
            addresses.add(PostalAddress.of(
                    addressTab[0],
                    addressTab[1],
                    addressTab[2],
                    addressTab[3],
                    addressTab[4]
            ));
        });
        return addresses;
    }
    private Set<Email> buildEmails(String input)
    {
        Set<Email> emails=new LinkedHashSet<>();
        separate(input, LIST_SEPARATOR).forEach(e->emails.add(Email.of(e)));
        return emails;
    }
    private Set<Phone> buildPhones(String input)
    {
        Set<Phone> phones=new LinkedHashSet<>();
        separate(input, LIST_SEPARATOR).forEach(p->phones.add(Phone.of(p)));
        return phones;
    }
    private List<String> separate(String input,String separator)
    {
        List<String> list=new ArrayList<>();
        String[] inputTab=input.split(separator);
        Collections.addAll(list, inputTab);
        return list;
    }
}
