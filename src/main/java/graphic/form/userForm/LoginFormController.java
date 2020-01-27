package graphic.form.userForm;

import graphic.components.input.FormTextField;
import graphic.form.FormQueryEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import tollmanager.model.identity.user.Login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginFormController implements Initializable {
    @FXML private FormTextField field;

    public LoginFormController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        field.focusedProperty().addListener((observableValue, old, isFocused) -> {
            if(old && !isFocused)
                field.fireEvent(FormQueryEvent.isLoginUsedEvent(getLogin()));
        });

        field.addEventHandler(FormQueryEvent.IS_LOGIN_USED,e->{
            if(e.getQuery().isUsed())
                field.setError("Login is already used");
        });
    }

    public Login getLogin() {
        try {
            return Login.of(field.getText());
        }catch (Exception e) {
            field.setError(e.getMessage());
        }
        return null;
    }


    public void setEditable(boolean editable) {
        field.setEditable(editable);
    }

    public void clearForm() {
        field.resetField();
    }

    public boolean isValid() {
        return !field.isError();
    }


    public void setVisible(boolean value) {

    }
}
