package graphic.components.team.tree.team;

import graphic.components.input.FormTextField;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import tollmanager.model.identity.team.TeamName;

public class AppendSubTeamPopup extends Dialog<ButtonType> {
    private FormTextField field;
    private Label errorLabel;

    public AppendSubTeamPopup() {
        super();
        initPane();
        initButton();
    }

    private void initButton(){
        getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
        Button validateButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        validateButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (teamName()==null )
                event.consume();
        });
    }

    private void initPane() {
        GridPane pane=new GridPane();
        pane.add(new Label("Team name:"),0,0);

        field=new FormTextField();
        pane.add(field,1,0);

        errorLabel=new Label();
        pane.add(errorLabel,0,1,2,1);
        getDialogPane().setContent(pane);
        getDialogPane().setMinWidth(350);

    }

    public TeamName teamName(){
        try {
            return TeamName.of(field.getText());
        }catch (Exception e) {
            field.setError(e.getMessage());
            errorLabel.setText(e.getMessage());
            setContentText(e.getMessage());
        }
        return null;
    }
}