package features.helper;

import tollmanager.infrastructure.persistance.inMemory.InMemoryTeamRepository;
import tollmanager.model.access.*;
import tollmanager.model.access.authorization.AuthorizationService;
import tollmanager.infrastructure.service.NullPasswordEncryption;
import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.identity.EmployeeProviderService;
import tollmanager.model.identity.EmployeeRepository;
import tollmanager.model.identity.IEmployeeProviderService;
import tollmanager.model.identity.team.TeamRepository;
import tollmanager.model.identity.user.*;
import tollmanager.model.identity.user.authentication.AuthenticationService;
import tollmanager.model.identity.user.authentication.IAuthenticationService;
import tollmanager.model.identity.user.password.IPasswordEncryptionService;
import tollmanager.infrastructure.persistance.inMemory.InMemoryEmployeeRepository;
import tollmanager.infrastructure.persistance.inMemory.InMemoryGroupRepository;
import tollmanager.infrastructure.persistance.inMemory.InMemoryUserRepository;

import java.util.List;

public class KnowTheDomain {
    private String errorMessage;
    private User connectedUser;

    private GroupRepository groupRepository;
    private IGroupService groupService;
    private IAuthorizationService authorizationService;

    private UserRepository userRepository;
    private IUserService userService;
    private IUserProviderService userProviderService;

    private EmployeeRepository employeeRepository;
    private IPasswordEncryptionService encryptionService;
    private IEmployeeProviderService employeeProviderService;

    private TeamRepository teamRepository;

    public GroupRepository groupRepository() {
        if(groupRepository==null)
            groupRepository=new InMemoryGroupRepository();
        return groupRepository;
    }
    public IGroupService groupService() {
        if(groupService==null)
            groupService=new GroupService(groupRepository(),authorizationService());
        return groupService;
    }
    public IAuthorizationService authorizationService() {
        if(authorizationService==null)
            authorizationService=new AuthorizationService(groupRepository());
        return authorizationService;
    }

    public UserRepository userRepository() {
        if(userRepository==null)
            userRepository=new InMemoryUserRepository();
        return userRepository;
    }
    public IUserService userService() {
        if(userService==null)
            userService=new UserService(userRepository(),groupService(),authorizationService());
        return userService;
    }
    public IUserProviderService userProviderService() {
        if(userProviderService==null)
            userProviderService=new UserProviderService(userRepository(),groupService(),authorizationService(),encryptionService());
        return userProviderService;
    }

    public EmployeeRepository employeeRepository() {
        if(employeeRepository==null)
            employeeRepository=new InMemoryEmployeeRepository();
        return employeeRepository;
    }
    public IPasswordEncryptionService encryptionService() {
        if(encryptionService==null)
            encryptionService=new NullPasswordEncryption();
        return encryptionService;
    }
    public IEmployeeProviderService employeeProviderService(){
        if(employeeProviderService==null)
            employeeProviderService=new EmployeeProviderService(employeeRepository(),authorizationService());
        return employeeProviderService;
    }

    public void setConnectedUser(User user)
    {
        connectedUser =user;
    }
    public User connectedUser() {
        if(connectedUser ==null)
            throw new IllegalStateException("no connected user found in steps");
        return connectedUser;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage=errorMessage;
    }
    public String errorMessage() {
        if(errorMessage==null)
            errorMessage="";
        return errorMessage;
    }


    public TeamRepository teamRepository() {
        if(teamRepository==null)
            teamRepository=new InMemoryTeamRepository();
        return teamRepository;
    }
}
