package tollmanager.model.identity.person;

import java.util.Objects;
/**
 * Represents the niss (belgium)
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Niss {
    private final String niss;
    public static final String REGEX="^\\d{2}\\.\\d{2}\\.\\d{2}\\-\\d{3}\\.\\d{2}$";

    private Niss(String niss) {
        this.niss = niss;
    }

    public static Niss of(String niss) {
        Objects.requireNonNull(niss,"The niss cannot be null.");
        if(!niss.matches(REGEX))
            throw new IllegalArgumentException("the niss format must be xx.xx.xx-xxx.xx");
        return new Niss(niss);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Niss)) return false;
        Niss other = (Niss) o;
        return Objects.equals(niss, other.niss);
    }

    @Override
    public int hashCode() {
        return Objects.hash(niss);
    }

    @Override
    public String toString() {
        return "Niss{" +
                "niss='" + niss + '\'' +
                '}';
    }

    public String value() {
        return niss;
    }
}
