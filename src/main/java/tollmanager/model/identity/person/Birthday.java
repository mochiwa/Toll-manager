package tollmanager.model.identity.person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
/**
 * Represents a birthday
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Birthday {
    public static final String US_YEARS_REGEX = "^([12]\\d{3}\\/(0[1-9]|1[0-2])\\/(0[1-9]|[12]\\d|3[01]))$";
    private final LocalDate birthday;

    private Birthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public static Birthday of(String birthday) {
        Objects.requireNonNull(birthday,"The birthday cannot be null.");
        String[] date=birthday.split("/");
        if(date.length<2)
            throw new IllegalArgumentException("The birthday format must be like yyyy/mm/dd.");
        return new Birthday(LocalDate.of(
                Integer.parseInt(date[0]),
                Integer.parseInt(date[1]),
                Integer.parseInt(date[2]))
        );
    }

    public static Birthday of(LocalDate birthday) {
        Objects.requireNonNull(birthday,"The birthday cannot be null.");
        return new Birthday(birthday);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Birthday)) return false;
        Birthday other = (Birthday) o;
        return Objects.equals(birthday.getYear(), other.birthday.getYear()) &&
                Objects.equals(birthday.getMonth(), other.birthday.getMonth()) &&
                Objects.equals(birthday.getDayOfMonth(), other.birthday.getDayOfMonth());
    }
    @Override
    public int hashCode() {
        return Objects.hash(birthday.getYear(),birthday.getMonth(),birthday.getDayOfMonth());
    }
    @Override
    public String toString() {
        return "Birthday{" +
                "birthday=" + birthday +
                '}';
    }

    public String value() {
        return birthday.format(DateTimeFormatter.ISO_DATE).replace('-','/');
    }
    public LocalDate valueToLocalDate(){return birthday;}
}
