package tollmanager.model.identity.person;

import tollmanager.model.identity.contact.ContactInformation;

import java.util.Objects;
/**
 * Represents a person composed by niss , fullname ,birthday and contact
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Person {
    private Niss niss;
    private FullName fullName;
    private Birthday birthday;
    private ContactInformation contactInformation;

    private Person( Niss niss, FullName fullName, Birthday birthday, ContactInformation contactInformation) {
        this.niss = niss;
        this.fullName = fullName;
        this.birthday = birthday;
        this.contactInformation = contactInformation;
    }

    public static Person of( Niss niss, FullName fullName, Birthday birthday, ContactInformation contactInformation) {
        Objects.requireNonNull(niss,"The niss is required.");
        Objects.requireNonNull(fullName,"The full name is required.");
        Objects.requireNonNull(birthday,"The birthday is required.");
        Objects.requireNonNull(contactInformation,"The contact information is required.");

        return new Person(niss, fullName, birthday, contactInformation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(niss, person.niss) &&
                Objects.equals(fullName, person.fullName) &&
                Objects.equals(birthday, person.birthday) &&
                Objects.equals(contactInformation, person.contactInformation);
    }
    @Override
    public int hashCode() {
        return Objects.hash( niss, fullName, birthday, contactInformation);
    }
    @Override
    public String toString() {
        return "Person{" +
                ", niss=" + niss +
                ", fullName=" + fullName +
                ", birthday=" + birthday +
                ", contactInformation=" + contactInformation +
                '}';
    }

    public Niss niss() {
        return niss;
    }

    public FullName fullName() {
        return fullName;
    }

    public Birthday birthday() {
        return birthday;
    }

    public ContactInformation contactInformation() {
        return contactInformation;
    }

    public String fullNameToString() {
        return fullName.name()+" "+fullName.forename();
    }
}
