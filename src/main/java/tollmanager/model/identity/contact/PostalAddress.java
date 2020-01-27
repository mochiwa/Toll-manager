package tollmanager.model.identity.contact;

import java.util.Objects;
/**
 * Represents a postal address
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class PostalAddress {
    private final String houseNumber;
    private final String street;
    private final String city;
    private final String zip;
    private final String country;

    private PostalAddress(String houseNumber, String street, String city, String zip, String country) {
        assertValid(houseNumber,"house number");
        assertValid(street,"street");
        assertValid(city,"city");
        assertValid(zip,"zip code");
        assertValid(country,"country");

        this.houseNumber = houseNumber;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.country = country;
    }

    public static PostalAddress of(String houseNumber, String street, String city, String zip, String country) {
        return new PostalAddress(houseNumber, street, city, zip, country);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostalAddress)) return false;
        PostalAddress other = (PostalAddress) o;
        return Objects.equals(houseNumber, other.houseNumber) &&
                Objects.equals(street, other.street) &&
                Objects.equals(city, other.city) &&
                Objects.equals(zip, other.zip) &&
                Objects.equals(country, other.country);
    }
    @Override
    public int hashCode() {
        return Objects.hash(houseNumber, street, city, zip, country);
    }
    @Override
    public String toString() {
        return "PostalAddress{" +
                "houseNumber='" + houseNumber + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    private void assertValid(String value,String field)
    {
        Objects.requireNonNull(value,"The "+field+" cannot be null.");
        if(value.trim().isEmpty() || value.length()>55)
            throw new IllegalArgumentException("The "+field+"'s length must be between 1 and 55.");
    }

    public String street() {
        return street;
    }

    public String number() {
        return houseNumber;
    }

    public String city() {
        return city;
    }

    public String zipCode() {
        return zip;
    }

    public String country() {
        return country;
    }
}