package tollmanager.application.query;

import tollmanager.model.identity.user.User;
/**
 * ask the business the user is a manager
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class IsManagerQuery {
    private User user;
    private boolean isManager;

    public IsManagerQuery() {
        this.user=null;
        isManager=false;
    }
    public IsManagerQuery(User user) {
        this();
        this.user=user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setIsManager(boolean manager) {
        isManager = manager;
    }
}
