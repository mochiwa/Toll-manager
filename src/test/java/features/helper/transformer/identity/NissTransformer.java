package features.helper.transformer.identity;

import features.helper.transformer.OwnTransformer;
import tollmanager.model.identity.person.Niss;

public class NissTransformer implements OwnTransformer {

    public static NissTransformer of() {
        return new NissTransformer();
    }

    public Niss fromStringToNiss(String input){
        input=input.replace(WORD_SEPARATOR, SPACE).trim();
        if(input.trim().isEmpty())
            return null;
        return Niss.of(input);
    }
}
