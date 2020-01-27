package tollmanager.model.identity.contact;

import java.util.Objects;
/**
 * Represents a phone
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Phone {
    public final static String MOBILE_FORMAT ="^(\\d{4}(\\.\\d{2}){3})$";
    private final static String phoneFormat="^(\\d{3}(\\.\\d{2}){3})$";
    private final String phoneNumber;
    private final String countryCode;

    private Phone(String phoneNumber,String countryCode) {
        this.phoneNumber= phoneNumber;
        this.countryCode=countryCode;
    }

    public static Phone of(String phone) {
        Objects.requireNonNull(phone,"the phone number cannot be null");
        if(phone.matches(MOBILE_FORMAT))
            return new Phone(phone,"+32");
        else if(phone.matches(phoneFormat))
            return new Phone(phone,"+32");
       else
            throw new IllegalArgumentException("Unknown phone format.");

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Phone)) return false;
        Phone phone = (Phone) o;
        return Objects.equals(phoneNumber, phone.phoneNumber) &&
                Objects.equals(countryCode, phone.countryCode);
    }
    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber, countryCode);
    }
    @Override
    public String toString() {
        return "Phone{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }

    public boolean isAMobile() {
        return phoneNumber.matches(MOBILE_FORMAT);
    }

    public String value() {
        return phoneNumber;
    }
}
