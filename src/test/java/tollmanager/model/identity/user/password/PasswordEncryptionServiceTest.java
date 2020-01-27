package tollmanager.model.identity.user.password;

import tollmanager.infrastructure.service.MD5PasswordEncryptionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PasswordEncryptionServiceTest {
    private IPasswordEncryptionService encryptionService;

    @Before
    public void setUp()
    {
        encryptionService=new MD5PasswordEncryptionService();
    }

    @Test
    public void encryption_shouldReturnEncryptedPassword() {
        Password clearPassword=Password.of("Secret123@");
        Password encryptedPassword=encryptionService.encryption(clearPassword);

        Assert.assertNotEquals(clearPassword,encryptedPassword);
    }

    @Test
    public void encryption_shouldReturnNonNullPassword(){
        Assert.assertNotNull(encryptionService.encryption(Password.of("Secret123@")));
    }

    @Test
    public void encryption_shouldReturnSameHashedPassword_whenSameClearPassword()
    {
        Assert.assertEquals(
                encryptionService.encryption(Password.of("Secret123@")),
                encryptionService.encryption(Password.of("Secret123@"))
        );
    }
}
