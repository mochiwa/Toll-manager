package tollmanager.model.identity;

import tollmanager.model.identity.contact.ContactInformation;
import tollmanager.model.identity.person.Birthday;
import tollmanager.model.identity.person.FullName;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.user.User;

public interface IEmployeeProviderService {

    /**
     * @param caller who's asked the service
     * @param niss the niss of the employee to register
     * @param fullName the full name of the employee to register
     * @param birthday the birthday of the employee to register
     * @param contactInformation contact information of the employee to register
     * @return the employee created
     */
    Employee registerEmployee(User caller, Niss niss, FullName fullName, Birthday birthday, ContactInformation contactInformation);
}
