package tollmanager.model.identity.user.password;

public interface IPasswordEncryptionService {
    Password encryption(Password password);
}
