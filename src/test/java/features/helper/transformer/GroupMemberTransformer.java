package features.helper.transformer;

import tollmanager.model.access.GroupMember;
import tollmanager.model.identity.EmployeeId;
import io.cucumber.datatable.TableEntryTransformer;

import java.util.*;

public class GroupMemberTransformer implements TableEntryTransformer<GroupMember> , OwnTransformer {
    private final String ID_SEPARATOR ="->";
    private final String MEMBER_SEPARATOR ="&&";


    static GroupMemberTransformer of() {
        return new GroupMemberTransformer();
    }

    GroupMember fromStringToGroupMember(String input) {
        String[] memberData=input.replace(WORD_SEPARATOR,SPACE).trim().split(ID_SEPARATOR);
        if(memberData.length<2)
            return null;
        return GroupMember.of(EmployeeId.of(memberData[0]),memberData[1]);
    }

    LinkedHashSet<GroupMember> fromStringToSet(String input) {
        LinkedHashSet<GroupMember> members= new LinkedHashSet<>();
        String[] inputSplit=input.split(MEMBER_SEPARATOR);

        for (String member : inputSplit) {
            member=member.replace(ARRAY_OPEN,SPACE).replace(ARRAY_CLOSE,SPACE).trim();
            GroupMember groupMember=fromStringToGroupMember(member);
            if(groupMember!=null)
                members.add(groupMember);
        }
        return members;
    }

    @Override
    public GroupMember transform(Map<String, String> map) throws Throwable {
        return GroupMember.of(EmployeeId.of(map.get("employeeId")),map.get("name"));
    }


}
