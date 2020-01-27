package tollmanager.application.query;

import tollmanager.model.identity.contact.Phone;
/**
 * ask the business if phone is already used
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class IsPhoneUsedQuery {
    private Phone phone;
    private boolean isUsed;


    public IsPhoneUsedQuery(Phone phone) {
        this.phone = phone;
        isUsed=false;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
