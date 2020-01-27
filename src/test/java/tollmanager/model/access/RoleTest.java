package tollmanager.model.access;

import org.junit.Test;

import static org.junit.Assert.*;

public class RoleTest {

    @Test
    public void equalityTest() {
        assertEquals(Role.create_team(),Role.create_team());
    }

    @Test
    public void isEquivalent_shouldReturnTrue_whenCompare_whileCard_to_Concrete()
    {
        Role appendWildCard=Role.append_member(GroupName.wildCard());
        Role appendToCompare=Role.append_member(GroupName.Null());

        assertTrue(appendWildCard.isEquivalent(appendToCompare));
    }

    @Test
    public void isEquivalent_shouldReturnFalse_whenCompare_concrete_to_wildCard()
    {
        Role appendWildCard=Role.append_member(GroupName.wildCard());
        Role appendToCompare=Role.append_member(GroupName.Null());

        assertFalse(appendToCompare.isEquivalent(appendWildCard));
    }
}
