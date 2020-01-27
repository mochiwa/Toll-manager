package tollmanager.model.identity.contact;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
/**
 * Represents the book of address from an employee
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class ContactInformation {
    private Set<PostalAddress> addresses;
    private Set<Email> emails;
    private Set<Phone> phones;

    private ContactInformation(Set<PostalAddress> addresses, Set<Email> emails, Set<Phone> phones) {
        this.addresses = addresses;
        this.emails = emails;
        this.phones = phones;
    }

    public static ContactInformation of(Set<PostalAddress> addresses, Set<Email> emails, Set<Phone> phones) {
        Objects.requireNonNull(addresses,"the address list cannot be null");
        Objects.requireNonNull(emails,"the email list cannot be null");
        Objects.requireNonNull(phones,"the phone list cannot be null");

        if(addresses.isEmpty())
            throw new IllegalArgumentException();
        if(emails.isEmpty())
            throw new IllegalArgumentException();
        if(phones.isEmpty())
            throw new IllegalArgumentException();

        return new ContactInformation(addresses,emails,phones);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactInformation)) return false;
        ContactInformation other = (ContactInformation) o;
        return Objects.equals(addresses, other.addresses) &&
                Objects.equals(emails, other.emails) &&
                Objects.equals(phones, other.phones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addresses, emails, phones);
    }
    @Override
    public String toString() {
        return "ContactInformation{" +
                "addresses=" + addresses +
                ", emails=" + emails +
                ", phones=" + phones +
                '}';
    }

    public void addAddress(PostalAddress address) {
        Objects.requireNonNull(address,"The address to append cannot be null.");
        addresses.add(address);
    }
    public void removeAddress(PostalAddress address){
        Objects.requireNonNull(address,"The address to remove is required.");
        addresses.remove(address);
    }
    public void addEmail(Email email) {
        Objects.requireNonNull(email,"The email to append cannot be null.");
        emails.add(email);
    }
    public void removeEmail(Email email){
        Objects.requireNonNull(email,"The email to remove is required.");
        emails.remove(email);
    }
    public void addPhone(Phone phone) {
        Objects.requireNonNull(phone,"The phone to append cannot be null.");
        phones.add(phone);
    }
    public void removePhone(Phone phone) {
        Objects.requireNonNull(phone,"The phone to remove is required.");
        phones.remove(phone);
    }

    public PostalAddress getAddress(int i) {
        return addresses.stream().findFirst().get();
    }

    public Phone getPhone(int i) {
        return phones.stream().findFirst().get();
    }

    public Email getEmail(int i) {
        return emails.stream().findFirst().get();
    }

    public Set<PostalAddress> addresses() {
        return new LinkedHashSet<>(addresses);
    }
    public Set<Email> emails() {
        return new LinkedHashSet<>(emails);
    }
    public Set<Phone> phones(){
        return new LinkedHashSet<>(phones);
    }
}
