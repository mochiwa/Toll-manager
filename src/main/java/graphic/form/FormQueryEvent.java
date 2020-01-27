package graphic.form;

import graphic.components.event.QueryEvent;
import javafx.event.EventType;
import tollmanager.application.query.IsEmailUsedQuery;
import tollmanager.application.query.IsLoginUsedQuery;
import tollmanager.application.query.IsNissUsedQuery;
import tollmanager.application.query.IsPhoneUsedQuery;
import tollmanager.model.identity.contact.Email;
import tollmanager.model.identity.contact.Phone;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.user.Login;

public class FormQueryEvent {
    public static final EventType<IsEmailUsedEvent> IS_EMAIL_USED=new EventType<>("IS_EMAIL_USED");
    public static final EventType<IsNissUsedEvent> IS_NISS_USED=new EventType<>("IS_NISS_USED");
    public static final EventType<IsPhoneUsedEvent> IS_PHONE_USED=new EventType<>("IS_PHONE_USED");
    public static final EventType<IsLoginUsedEvent> IS_LOGIN_USED=new EventType<>("IS_LOGIN_USED");


    public static QueryEvent isEmailUsedEvent(Email email){
        IsEmailUsedQuery query=new IsEmailUsedQuery(email);
        return new IsEmailUsedEvent(query);
    }

    public static QueryEvent isNissUsedEvent(Niss niss){
        IsNissUsedQuery query=new IsNissUsedQuery(niss);
        return new IsNissUsedEvent(query);
    }

    public static QueryEvent isPhoneUsedEvent(Phone phone){
        IsPhoneUsedQuery query=new IsPhoneUsedQuery(phone);
        return new IsPhoneUsedEvent(query);
    }

    public static QueryEvent isLoginUsedEvent(Login login){
        IsLoginUsedQuery query=new IsLoginUsedQuery(login);
        return new IsLoginUsedEvent(query);
    }



    public static class IsEmailUsedEvent extends QueryEvent<IsEmailUsedQuery> {
        private IsEmailUsedEvent(IsEmailUsedQuery query){
            super(IS_EMAIL_USED);
            setQuery(query);
        }
    }

    public static class IsNissUsedEvent extends QueryEvent<IsNissUsedQuery> {
        private IsNissUsedEvent(IsNissUsedQuery query){
            super(IS_NISS_USED);
            setQuery(query);
        }
    }

    public static class IsPhoneUsedEvent extends QueryEvent<IsPhoneUsedQuery> {
        private IsPhoneUsedEvent(IsPhoneUsedQuery query){
            super(IS_PHONE_USED);
            setQuery(query);
        }
    }

    public static class IsLoginUsedEvent extends QueryEvent<IsLoginUsedQuery> {
        private IsLoginUsedEvent(IsLoginUsedQuery query){
            super(IS_LOGIN_USED);
            setQuery(query);
        }
    }
}
