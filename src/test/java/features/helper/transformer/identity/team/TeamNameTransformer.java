package features.helper.transformer.identity.team;

import features.helper.transformer.OwnTransformer;
import tollmanager.model.identity.team.TeamName;

public class TeamNameTransformer implements OwnTransformer {

    public static TeamNameTransformer of() {
        return new TeamNameTransformer();
    }

    public TeamName fromStringToTeamName(String input)
    {
        input=input.replace(WORD_SEPARATOR,SPACE).trim();
        return TeamName.of(input);
    }
}
