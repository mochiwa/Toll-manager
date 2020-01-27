package tollmanager.infrastructure.service;

import tollmanager.model.identity.user.password.IPasswordEncryptionService;
import tollmanager.model.identity.user.password.Password;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class MD5PasswordEncryptionService implements IPasswordEncryptionService {
    /**
     * Encrypt a password using MD5
     * @param clearPassword
     * @return
     */
    @Override
    public Password encryption(Password clearPassword) {
        StringBuilder stringBuilder=new StringBuilder();;
        MessageDigest messageDigest;
        Password hashedPassword=null;
        byte[] bytes;

        try{
            messageDigest=MessageDigest.getInstance("MD5");
            messageDigest.update(clearPassword.toArrayBytes());
            bytes=messageDigest.digest();
            for (byte aByte : bytes)
                stringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            hashedPassword=Password.of(stringBuilder.toString());
        }catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        Objects.requireNonNull(hashedPassword,"A error occurs during the encryption");
        return hashedPassword;
    }
}
