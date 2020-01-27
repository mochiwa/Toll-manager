package features.helper.transformer.identity;

import features.helper.transformer.OwnTransformer;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.person.Niss;

import java.util.LinkedHashSet;

public class EmployeeIdTransformer implements OwnTransformer {

    public static EmployeeIdTransformer of() {
        return new EmployeeIdTransformer();
    }

    public LinkedHashSet<EmployeeId> FromStringToSet(String input) {
        LinkedHashSet<EmployeeId> ids= new LinkedHashSet<>();

        String[] inputSplit= input.replace(ARRAY_OPEN,SPACE).replace(ARRAY_CLOSE,SPACE).trim().split(LIST_SEPARATOR);

        if(inputSplit[0].isEmpty())
            return ids;

        for (String id : inputSplit)
            ids.add(EmployeeId.of(id));

        return ids;
    }

    public EmployeeId fromStringToEmployeeId(String input){
        input=input.replace(WORD_SEPARATOR, SPACE).trim();
        return EmployeeId.of(input);
    }
}
