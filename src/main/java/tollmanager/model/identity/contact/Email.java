package tollmanager.model.identity.contact;

import java.util.Objects;
/**
 * Represents an email
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Email {
    public static final String REGEX= "^[a-zA-Z0-9_.+]*\\@[a-z0-9-_.+]*\\.[a-z]{2,4}$";
    private final String email;

    private Email(String email) {
        this.email = email;
    }

    /**
     * Email must respect the regex of an email : xxxx@xxxx.xxx
     * @exception if email in argument doesn't match the regex
     * @param email
     * @return Email
     */
    public static Email of(String email) {
        Objects.requireNonNull(email,"The email cannot be null.");
        if(!email.matches(REGEX))
            throw new IllegalArgumentException("The email must be like xxxx@xxx.xxx .");
        return new Email(email.toLowerCase().trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email1 = (Email) o;
        return Objects.equals(email, email1.email);
    }
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    @Override
    public String toString() {
        return "Email{" +
                "email='" + email + '\'' +
                '}';
    }

    public String value() {
        return email;
    }
}
