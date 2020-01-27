package tollmanager.model.identity;

import tollmanager.model.identity.contact.Phone;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneTest {

    @Test
    public void factory_shouldBuildMobile_whenFormatMatches()
    {
        String brutPhone="0478.62.62.62";
        Phone phone=Phone.of(brutPhone);
        assertTrue(phone.isAMobile());
    }

    @Test
    public void factory_shouldBuildPhone_whenFormatMatches()
    {
        String brutPhone="010.45.45.10";
        Phone phone=Phone.of(brutPhone);
        assertFalse(phone.isAMobile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void factory_shouldBIllegalArgumentException_whenFormatNoMatches()
    {
        String brutPhone="010454545";
        Phone phone=Phone.of(brutPhone);
    }
}
