package features.helper.transformer;

import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.password.Password;
import tollmanager.model.identity.user.User;
import io.cucumber.datatable.TableEntryTransformer;

import java.util.Map;

public class UserTransformer implements TableEntryTransformer<User> , OwnTransformer {


    static UserTransformer of()
    {
        return new UserTransformer();
    }

    Login login(String input) {

        input=input.replace(WORD_SEPARATOR,SPACE).trim();
        if(input.trim().isEmpty())
            return null;
        return Login.of(input);
    }

    Password password(String input) {
        input=input.replace(WORD_SEPARATOR,SPACE).trim();
        return Password.of(input);
    }

    @Override
    public User transform(Map<String, String> map) throws Throwable {
        return User.of(EmployeeId.of(map.get("employeeId")), Login.of(map.get("login")), Password.of(map.get("password")));
    }
}
