package graphic.form.team;

import graphic.components.input.FormTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Pair;
import tollmanager.model.identity.team.*;

import java.io.IOException;


public class RootTeamRegisterDialog extends Dialog<Pair<TeamName,String>> {
    @FXML private FormTextField fieldName;
    @FXML private TextArea fieldDescription;

    public RootTeamRegisterDialog() {
        initPane();

        setTitle("Root team register");
        initButtons();
        initReturnConverter();
    }

    private void initPane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("teamForm.fxml"));
        loader.setController(this);
        try {
            getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initButtons() {
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Button validateButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        validateButton.setText("Register");
        validateButton.setId("validateButton");
        validateButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (teamName()==null)
                event.consume();
        });
    }

    private void initReturnConverter() {
        setResultConverter(buttonType -> {
            if(buttonType==ButtonType.OK)
                return new Pair<>(teamName(),description());
            return null;
        });
    }

    public TeamName teamName() {
        try {
            return TeamName.of(fieldName.getText());
        }catch (Exception e){
            fieldName.setError(e.getMessage());

        }
        return null;
    }
    public String description() {
        return fieldDescription.getText();
    }
}
