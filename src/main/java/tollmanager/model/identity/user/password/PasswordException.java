package tollmanager.model.identity.user.password;

public class PasswordException extends IllegalArgumentException {
    public enum CODE{NOTSECURE,NOTMATCH,OTHER};

    private PasswordException(String msg) {
        super(msg);
    }

    public PasswordException(CODE code) {
        if(code.equals(CODE.NOTSECURE))
            throw new PasswordException("The password must contain 5 to 55 characters , one or more Uppercase , one ore more numbers and one ore more of #?!@$%^&*-");
        else if(code.equals(CODE.NOTMATCH))
            throw new PasswordException("Password does not match.");
        else
            throw new PasswordException("Unknown password error.");
    }
}
