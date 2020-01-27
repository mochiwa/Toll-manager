package tollmanager.model.identity.user;

import java.util.Objects;
/**
 * Value object for login
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Login {
    private final String login;

    private Login(String login) {
        this.login = login;
    }

    public static Login of(String login) {
        Objects.requireNonNull(login,"The login cannot be null");
        if(login.trim().length()<3 || login.trim().length()>55)
            throw new IllegalArgumentException("the login's length must between 3 and 55");
        if(!login.matches("[A-Za-z0-9-_]{3,55}"))
            throw new IllegalArgumentException("the login can have one or more Uppercase , one ore more number , one or more -_");
        return new Login(login);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Login)) return false;
        Login other = (Login) o;
        return Objects.equals(login,other.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return "Login{" +
                "login='" + login + '\'' +
                '}';
    }

    public String value() {
        return login;
    }
}
