package tollmanager.application;


public class ApplicationLifeTimeTest {
   /* private GroupRepository groupRepository;
    private EmployeeRepository employeeRepository;
    private UserRepository userRepository;
    private TeamRepository teamRepository;


    private ApplicationLifeTime app;

    @Before
    public void setUp() {
        groupRepository=new InMemoryGroupRepository();
        employeeRepository=new InMemoryEmployeeRepository();
        userRepository=new InMemoryUserRepository();
        teamRepository=new InMemoryTeamRepository();

        app=new ApplicationLifeTime(groupRepository,employeeRepository,userRepository,teamRepository);
    }

    @Test
    public void isFirstLaunch_ShouldReturnTrue_whenNotAdministratorGroupInRepository() {
        assertTrue(app.isFirstLaunch());
    }
    @Test
    public void isFirstLaunch_ShouldReturnTrue_whenNotMemberInAdministratorGroup() {
        groupRepository.add(GroupBuilder.of().setName(ApplicationLifeTime.administratorGroup).create());
        assertTrue(app.isFirstLaunch());
    }
    @Test
    public void initApplicationForFirstLaunch_shouldCreateAdminGroup_whenItIsFirstLaunch() {
        app.initFirstLaunchGroups();
        assertNotNull(groupRepository.findByName(ApplicationLifeTime.administratorGroup));
    }
    @Test
    public void initApplicationForFirstLaunch_shouldCreateManagerGroup_whenItIsFirstLaunch() {
        app.initFirstLaunchGroups();
        assertNotNull(groupRepository.findByName(ApplicationLifeTime.managerGroup));
    }
    @Test
    public void initApplicationForFirstLaunch_shouldCreateTeamLeaderGroup_whenItIsFirstLaunch() {
        app.initFirstLaunchGroups();
        assertNotNull(groupRepository.findByName(ApplicationLifeTime.teamLeaderGroup));
    }

    @Test
    public void initApplicationForFirstLaunch_shouldProvideAdministrator_whenItIsFirstLaunch() {
        app.initApplicationForFirstLaunch(TestHelper.of().getPerson("john","doe"));

        assertNotNull(userRepository.findByLogin(ApplicationLifeTime.administratorLogin));
    }
    @Test
    public void initApplicationForFirstLaunch_shouldAppendAdministratorToAdminGroup_whenItIsFirstLaunch() {
        app.initApplicationForFirstLaunch(TestHelper.of().getPerson("john","doe"));
        Group adminGroup=groupRepository.findByName(ApplicationLifeTime.administratorGroup);
        User user=userRepository.findByLogin(ApplicationLifeTime.administratorLogin);
        assertTrue(adminGroup.hasMember(user.toGroupMember()));
    }

    @Test
    public void logout_shouldDisconnectTheUserFromTheApplication() {
        User user=User.of(EmployeeId.of("aaa"), Login.of("admin"), Password.of("admin"));
        app.initApplication(user);
        assertEquals(user,app.connectedUser());
        app.logout();
        assertNull(app.connectedUser());
    }*/
}
