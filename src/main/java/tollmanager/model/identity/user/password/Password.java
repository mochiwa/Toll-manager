package tollmanager.model.identity.user.password;

import java.util.Objects;

public class Password {
    private final String password;

    private Password(String password) {
        this.password = password;
    }


    public static Password of(String password) {
        Objects.requireNonNull(password,"The password cannot be null");

       if(password.trim().length()<3 || password.trim().length()>150)
           throw new IllegalArgumentException("The password's length should be between 5 and 150.");
       return new Password(password);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Password)) return false;
        Password other = (Password) o;
        return Objects.equals(password, other.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }

    public byte[] toArrayBytes() {
        return password.getBytes();
    }

    /**
     * Password is secure if it matches to the regex Maj + numeric + special char and min 5 length
     * @return
     */
    public boolean isSecure() {
        return password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{5,55}$");
    }

    @Override
    public String toString() {
        return "Password{" +
                "password='" + password + '\'' +
                '}';
    }

    public String value() {
        return password;
    }
}
