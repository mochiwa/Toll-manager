package tollmanager.model.identity.team;

import org.junit.Assert;
import org.junit.Test;
import tollmanager.TestHelper;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.user.User;

import static org.junit.Assert.*;

public class TeamTest {



    @Test
    public void appendEmployee_shouldAppendEmployeeToParentRecursively_AsFarAsRoot() {
        User leader= TestHelper.of().getUser("01","aLeader");

        Team rootTeam=TestHelper.of().getTeam("AAA","root-a",leader);
        Team subTeam_A=rootTeam.appendSubTeam(TeamId.of("AAA-a"),TeamName.of("sub-a"));
        Team subTeam_AB=subTeam_A.appendSubTeam(TeamId.of("AAA-aa"),TeamName.of("sub-aa"));
        Employee emp=TestHelper.of().getEmployee("a","employeeA","EmployeeA");

        subTeam_AB.appendEmployee(emp);




        assertTrue(rootTeam.hasEmployee(emp));
        assertTrue(subTeam_A.hasEmployee(emp));
        assertTrue(subTeam_AB.hasEmployee(emp));
    }

    @Test
    public void removeEmployee_shouldRemoveEmployeeFromAllSubTeam() {
        User leader = TestHelper.of().getUser("01", "aLeader");

        Team rootTeam = TestHelper.of().getTeam("AAA", "root-a", leader);
        Team subTeam_A = rootTeam.appendSubTeam(TeamId.of("AAA-a"), TeamName.of("sub-a"));
        Team subTeam_AB = subTeam_A.appendSubTeam(TeamId.of("AAA-aa"), TeamName.of("sub-aa"));
        Employee emp = TestHelper.of().getEmployee("a", "employeeA", "EmployeeA");
        subTeam_AB.appendEmployee(emp);

        rootTeam.removeEmployee(emp);


        assertFalse(rootTeam.hasEmployee(emp));
        assertFalse(subTeam_A.hasEmployee(emp));
        assertFalse(subTeam_AB.hasEmployee(emp));
    }



    @Test
    public void leafEmployees_shouldReturnOneEmployee_whenOneEmployee_NotBelong_ToASubTeam(){
        User leader= TestHelper.of().getUser("01","aLeader");
        Team team=TestHelper.of().getTeam("TeamA","teamA",leader);

        Employee emp=TestHelper.of().getEmployee("02","employee","employee");
        team.appendEmployee(emp);

        assertEquals(1,team.leafEmployees().size());
        assertTrue(team.leafEmployees().contains(emp));
    }
    @Test
    public void leafEmployee_shouldNotReturnOneEmployee_whenNoOnEmployee_Belong_ToASubTeam() {
        User leader= TestHelper.of().getUser("01","aLeader");
        Team team=TestHelper.of().getTeam("TeamA","teamA",leader);
        Team subTeam=team.appendSubTeam(TeamId.of("teamA-1"),TeamName.of("teamA-1"));

        Employee emp=TestHelper.of().getEmployee("02","employee","employee");
        subTeam.appendEmployee(emp);

        assertTrue(team.hasEmployee(emp));
        assertTrue(team.leafEmployees().isEmpty());
        assertTrue(subTeam.leafEmployees().contains(emp));
    }

    @Test
    public void hasTeam_shouldReturnTrue_whenSubTeamBelongToParentTeam() {
        User leader= TestHelper.of().getUser("01","aLeader");
        Team team=TestHelper.of().getTeam("TeamA","teamA",leader);
        Team subTeam=team.appendSubTeam(TeamId.of("teamA-1"),TeamName.of("teamA-1"));

        assertTrue(team.hasTeam(subTeam.name()));
    }
    @Test
    public void hasTeam_shouldReturnTrue_whenSubTeamBelongToGrandParentTeam() {
        User leader= TestHelper.of().getUser("01","aLeader");
        Team root=TestHelper.of().getTeam("TeamA","teamA",leader);
        Team subTeam=root.appendSubTeam(TeamId.of("teamA-1"),TeamName.of("teamA-1"));
        Team grandSon=subTeam.appendSubTeam(TeamId.of("teamA-2"),TeamName.of("teamA-2"));

        assertTrue(root.hasTeam(grandSon.name()));
    }
    @Test
    public void hasTeam_shouldReturnFalse_whenSubTeam_notBelongToParent() {
        User leader= TestHelper.of().getUser("01","aLeader");
        Team root=TestHelper.of().getTeam("TeamA","teamA",leader);
        Team subTeam=root.appendSubTeam(TeamId.of("teamA-1"),TeamName.of("teamA-1"));
        Team anotherSubTeam=root.appendSubTeam(TeamId.of("teamA-2"),TeamName.of("teamA-2"));

        assertTrue(root.hasTeam(anotherSubTeam.name()));
        assertFalse(subTeam.hasTeam(anotherSubTeam.name()));
    }


    @Test
    public void removeTeam_shouldRemoveTeam() {
        User leader= TestHelper.of().getUser("01","aLeader");
        Team root=TestHelper.of().getTeam("TeamA","teamA",leader);
        Team subTeam=root.appendSubTeam(TeamId.of("teamA-1"),TeamName.of("teamA-1"));

        root.removeTeam(subTeam);
        assertFalse(root.hasTeam(subTeam.name()));
    }

    @Test
    public void removeTeam_shouldRemoveTeamAllChildren() {
        User leader= TestHelper.of().getUser("01","aLeader");
        Team root=TestHelper.of().getTeam("TeamA","teamA",leader);
        Team a=root.appendSubTeam(TeamId.of("teamA-1"),TeamName.of("teamA-1"));
        Team b=a.appendSubTeam(TeamId.of("teamA-2"),TeamName.of("teamA-2"));

        a.removeTeam(b);
        assertFalse(a.hasTeam(b.name()));
        assertFalse(root.hasTeam(b.name()));
    }
    @Test
    public void removeTeam_shouldRemoveTeamAllChildrenWhenRemoveOneParent() {
        User leader= TestHelper.of().getUser("01","aLeader");
        Team root=TestHelper.of().getTeam("root","rot",leader);
        Team a=root.appendSubTeam(TeamId.of("aaa"),TeamName.of("aaaa"));
        Team b=a.appendSubTeam(TeamId.of("bbb"),TeamName.of("bbbb"));

        assertTrue(root.hasTeam(b.name()));
        assertTrue(a.hasTeam(b.name()));

        root.removeTeam(b);

        assertFalse(root.hasTeam(b.name()));
        assertFalse(a.hasTeam(b.name()));
    }

    @Test
    public void hasTeamLeader_shouldReturnTrue_whenUserIsLeader() {
        User leader= TestHelper.of().getUser("01","aLeader");
        Team root=TestHelper.of().getTeam("root","rot",leader);

        assertTrue(root.hasTeamLeader(leader));
    }

    @Test
    public void hasTeamLeader_shouldReturnTrue_whenUserIsLeaderOfParent() {
        User leader= TestHelper.of().getUser("01","aLeader");
        Team root=TestHelper.of().getTeam("root","rot",leader);
        Team a=root.appendSubTeam(TeamId.of("aaa"),TeamName.of("aaaa"));
        Team b=a.appendSubTeam(TeamId.of("bbb"),TeamName.of("bbbb"));
        a.changeLeader(TestHelper.of().getUser("02","dontCare"));
        b.changeLeader(a.leader());

        assertTrue(b.hasTeamLeader(leader));
    }
    @Test
    public void hasTeamLeader_shouldReturnTrue_whenUserIsLeaderOfGrandParent() {
        User leader= TestHelper.of().getUser("01","aLeader");
        Team root=TestHelper.of().getTeam("root","rot",leader);
        Team a=root.appendSubTeam(TeamId.of("aaa"),TeamName.of("aaaa"));
        a.changeLeader(TestHelper.of().getUser("02","dontCare"));

        assertTrue(a.hasTeamLeader(leader));
    }

}
