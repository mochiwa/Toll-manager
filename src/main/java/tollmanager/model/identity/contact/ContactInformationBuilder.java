package tollmanager.model.identity.contact;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
/**
 * pattern builder for contact information
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class ContactInformationBuilder {
    private Set<PostalAddress> addresses;
    private Set<Email> emails;
    private Set<Phone> phones;

    private ContactInformationBuilder()
    {
        addresses=new LinkedHashSet<>();
        emails=new LinkedHashSet<>();
        phones=new LinkedHashSet<>();
    }

    public static ContactInformationBuilder of() {
        return new ContactInformationBuilder();
    }


    public ContactInformationBuilder setAddresses(Set<PostalAddress> addresses) {
        Objects.requireNonNull(addresses,"The address list cannot be null");
        this.addresses = addresses;
        return this;
    }
    public ContactInformationBuilder addAddress(PostalAddress address) {
        Objects.requireNonNull(address,"The address cannot be null");
        addresses.add(address);
        return this;
    }

    public ContactInformationBuilder setEmails(Set<Email> emails) {
        Objects.requireNonNull(emails,"The email list cannot be null");
        this.emails = emails;
        return this;
    }
    public ContactInformationBuilder addEmail(Email email) {
        Objects.requireNonNull(email,"The email list cannot be null");
        emails.add(email);
        return this;
    }

    public ContactInformationBuilder setPhones(Set<Phone> phones) {
        Objects.requireNonNull(phones,"The phone list cannot be null");
        this.phones = phones;
        return this;
    }
    public ContactInformationBuilder addPhone(Phone phone) {
        Objects.requireNonNull(phone,"The phone  cannot be null");
        this.phones.add(phone);
        return this;
    }

    public ContactInformation create() {
        return ContactInformation.of(addresses, emails, phones);
    }
}