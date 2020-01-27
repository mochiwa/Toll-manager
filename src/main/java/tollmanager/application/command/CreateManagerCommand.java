package tollmanager.application.command;


import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.password.Password;
/**
 * Represents the use case : create a new manager
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class CreateManagerCommand {
    private Person person;
    private Login login;
    private Password password;

    public CreateManagerCommand(Person person, Login login, Password password) {
        this.person=person;
        this.login=login;
        this.password=password;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }
}
