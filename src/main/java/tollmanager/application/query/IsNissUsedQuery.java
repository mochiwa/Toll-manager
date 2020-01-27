package tollmanager.application.query;

import tollmanager.model.identity.person.Niss;
/**
 * ask the business if login is already used
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class IsNissUsedQuery {
    private Niss niss;
    private boolean isUsed;

    public IsNissUsedQuery(Niss niss) {
        this.niss = niss;
        isUsed=false;
    }

    public Niss getNiss() {
        return niss;
    }

    public void setNiss(Niss niss) {
        this.niss = niss;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
