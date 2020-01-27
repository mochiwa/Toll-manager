package tollmanager.application.query;

import tollmanager.model.identity.user.Login;
/**
 * ask the business if login is already used
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class IsLoginUsedQuery {
    private Login login;
    private boolean isUsed;


    public IsLoginUsedQuery(Login login) {

        this.login = login;
        isUsed=false;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
