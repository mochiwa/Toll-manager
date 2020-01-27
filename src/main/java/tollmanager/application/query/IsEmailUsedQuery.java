package tollmanager.application.query;

import graphic.components.event.CommandEvent;
import javafx.event.Event;
import tollmanager.application.command.AppendSubTeamCommand;
import tollmanager.model.identity.contact.Email;
/**
 * ask the business if email is already used
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class IsEmailUsedQuery {
    private Email email;
    private boolean isUsed;


    public IsEmailUsedQuery(Email email) {
        this.email=email;
    }


    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
