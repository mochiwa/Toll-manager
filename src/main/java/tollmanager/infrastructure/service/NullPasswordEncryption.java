package tollmanager.infrastructure.service;

import tollmanager.model.identity.user.password.IPasswordEncryptionService;
import tollmanager.model.identity.user.password.Password;

public class NullPasswordEncryption implements IPasswordEncryptionService {
    /**
     * Stay a password clear
     * @param password
     * @return
     */
    @Override
    public Password encryption(Password password) {
        return password;
    }
}
