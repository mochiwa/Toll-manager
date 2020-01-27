package tollmanager.model.access;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroupNameTest {


    @Test (expected = IllegalArgumentException.class)
    public void shouldThrow_whenNameLengthIsLowerThen3Characters() {
        GroupName.of("aa");
    }
    @Test (expected = IllegalArgumentException.class)
    public void shouldThrow_whenNameLengthIsUpperThan55Characters() {
        String name="";
        for(int i=0;i<60;++i)
            name+='a';
        GroupName.of(name);
    }
    @Test
    public void of_shouldLowerCaseAllCharacter() {
        String name="HELLO";
        GroupName groupName=GroupName.of(name);
        Assert.assertNotEquals(groupName.value(),name);
        assertEquals(groupName.value(),name.toLowerCase());
    }

    @Test
    public void Null_shouldReturnAnEmptyGroupName() {
        assertEquals("",GroupName.Null().value());
    }

    @Test
    public void wildCard_shouldReturnAWildCardGroupName() {
        assertEquals("*",GroupName.wildCard().value());
    }

    @Test
    public void equalityTest() {
        GroupName groupName=GroupName.of("name");
        GroupName groupNameBis=GroupName.of("name");
        assertEquals(groupName,groupNameBis);
        assertEquals(groupName,groupName);
        assertEquals(groupName.hashCode(),groupNameBis.hashCode());
        System.err.println("end of test :"+groupName);
    }
}
