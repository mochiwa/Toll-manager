package features.helper.transformer;

import tollmanager.model.access.*;
import io.cucumber.datatable.TableEntryTransformer;

import java.util.LinkedHashSet;
import java.util.Map;

public class GroupTransformer implements TableEntryTransformer<Group>, OwnTransformer {

    public static GroupTransformer of() {
        return new GroupTransformer();
    }

    public GroupName fromStringToGroupName(String input)
    {
        input=input.replace(WORD_SEPARATOR,SPACE).trim();
        return GroupName.of(input);
    }

    @Override
    public Group transform(Map<String, String> map) throws Throwable {
        LinkedHashSet<Role> roles=getRoles(map.get("roles").split(","),map.get("groupToDo").split(","));
        LinkedHashSet<GroupMember> members = GroupMemberTransformer.of().fromStringToSet(map.get("members"));

        return GroupBuilder.of()
                .setName(map.get("name"))
                .setRoles(roles)
                .setMembers(members)
                .create();
    }

    private Role roleFactory(String role,String target) {
        GroupName groupNameTarget=null;
        if(!role.trim().equals(""))
            groupNameTarget= (target.equals("*")) ? GroupName.wildCard() : GroupName.of(target);

        switch (role) {
            case "append_member":
                return Role.append_member(groupNameTarget);
            case "remove_member":
                return Role.remove_member(groupNameTarget);
            case "manage_team":
                return Role.manage_team(groupNameTarget);
            case "create_employee":
                return Role.createEmployee();
            case "create_user":
                return Role.createUser(groupNameTarget);
            case "delete_user":
                return Role.deleteUser(groupNameTarget);
            case "create_team":
                return Role.create_team();
        }
        return Role.nullRole();
    }

    private LinkedHashSet<Role> getRoles(String[] rolesTab, String[] targetTab ) {
        LinkedHashSet<Role> roles=new LinkedHashSet<>();
        if(rolesTab.length==0)
            return roles;

        if(rolesTab.length!=targetTab.length)
            throw new IllegalArgumentException("The count of role("+rolesTab.length+") is not same as targetRole("+targetTab.length+")");

        for(int i=0;i<rolesTab.length;++i)
            roles.add(roleFactory(rolesTab[i],targetTab[i]));
        return roles;
    }
}
