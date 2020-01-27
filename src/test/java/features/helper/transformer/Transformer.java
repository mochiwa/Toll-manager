package features.helper.transformer;


import features.helper.transformer.identity.EmployeeIdTransformer;
import features.helper.transformer.identity.EmployeeTransformer;
import features.helper.transformer.identity.NissTransformer;
import features.helper.transformer.identity.team.TeamNameTransformer;
import io.cucumber.cucumberexpressions.ParameterType;
import tollmanager.model.access.Group;
import tollmanager.model.access.GroupMember;
import tollmanager.model.access.GroupName;
import tollmanager.model.identity.Employee;

import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.team.TeamName;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;
import tollmanager.model.identity.user.password.Password;


import java.util.LinkedHashSet;
import java.util.Locale;

public class Transformer  implements TypeRegistryConfigurer , OwnTransformer{
    private final String WORD_REGEX="\".*?\"";
    private final String ARRAY_REGEX="\\[ .*? \\]";


    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
        registerEmployeeId(typeRegistry);
        registerGroupMember(typeRegistry);
        registerGroup(typeRegistry);
        registerEmployee(typeRegistry);
        registerUser(typeRegistry);
        registerTeam(typeRegistry);
    }

    private void registerGroupMember(TypeRegistry typeRegistry) {
        GroupMemberTransformer transformer= GroupMemberTransformer.of();
        DataTableType groupMemberType=new DataTableType(GroupMember.class,transformer);
        typeRegistry.defineDataTableType(groupMemberType);

        typeRegistry.defineParameterType(new ParameterType<>("groupMember", WORD_REGEX, GroupMember.class,
                (String input)->GroupMemberTransformer.of().fromStringToGroupMember(input)));

        typeRegistry.defineParameterType(new ParameterType<>("groupMembers", ARRAY_REGEX, LinkedHashSet.class,
                (String input)->GroupMemberTransformer.of().fromStringToSet(input)));
    }
    private  void registerGroup(TypeRegistry typeRegistry) {
        GroupTransformer transformer= GroupTransformer.of();
        DataTableType groupType=new DataTableType(Group.class,transformer);
        typeRegistry.defineDataTableType(groupType);

        typeRegistry.defineParameterType(new ParameterType<>("groupName", ".*?", GroupName.class,
                (String input)->GroupTransformer.of().fromStringToGroupName(input)));
    }

    private void registerEmployee(TypeRegistry typeRegistry) {
        EmployeeTransformer transformer=new EmployeeTransformer();
        DataTableType employeeType=new DataTableType(Employee.class,transformer);
        typeRegistry.defineDataTableType(employeeType);
        registerNiss(typeRegistry);
    }
    private void registerNiss(TypeRegistry typeRegistry) {
        typeRegistry.defineParameterType(new ParameterType<>("niss", WORD_REGEX, Niss.class,
                (String input)-> NissTransformer.of().fromStringToNiss(input)));
    }
    private void registerEmployeeId(TypeRegistry typeRegistry) {
        typeRegistry.defineParameterType(new ParameterType<>("id", WORD_REGEX, EmployeeId.class,
                (String input)-> EmployeeIdTransformer.of().fromStringToEmployeeId(input)));

        typeRegistry.defineParameterType(new ParameterType<>("ids", ARRAY_REGEX, LinkedHashSet.class,
                (String input)-> EmployeeIdTransformer.of().FromStringToSet(input)));
    }

    private void registerUser(TypeRegistry typeRegistry) {
        UserTransformer transformer=new UserTransformer();
        DataTableType userType=new DataTableType(User.class,transformer);
        typeRegistry.defineDataTableType(userType);


        registerLogin(typeRegistry);
        registerPassword(typeRegistry);
    }
    private void registerLogin(TypeRegistry typeRegistry) {
        typeRegistry.defineParameterType(new ParameterType<>("login", WORD_REGEX, Login.class,
                (String input)->UserTransformer.of().login(input)));
    }
    private void registerPassword(TypeRegistry typeRegistry) {
        typeRegistry.defineParameterType(new ParameterType<>("password", WORD_REGEX, Password.class,
                (String input)->UserTransformer.of().password(input)));
    }

    private void registerTeam(TypeRegistry typeRegistry) {
        typeRegistry.defineParameterType(new ParameterType<>("teamName",WORD_REGEX, TeamName.class,
                (String input)-> TeamNameTransformer.of().fromStringToTeamName(input)));
    }

}
