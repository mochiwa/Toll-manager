package graphic.form.userForm;

import graphic.components.input.FormTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import tollmanager.model.identity.user.password.Password;
import tollmanager.model.identity.user.password.PasswordException;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PasswordFormController implements Initializable {
    @FXML private GridPane mainPane;
    @FXML private  TextField fieldPassword;
    @FXML private TextField fieldConfirmation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    public Pair<Password,Password> password() {
        try{
            Password password=Password.of(fieldPassword.getText());
            Password confirmation=Password.of(fieldConfirmation.getText());
            if(!password.isSecure() || !confirmation.isSecure() || !password.equals(confirmation))
                throw new PasswordException(PasswordException.CODE.OTHER);
            return new Pair<>(password,confirmation);
        }catch (Exception t) {
        }
        fieldPassword.setText("");
        fieldConfirmation.setText("");
        return null;
    }

    public void clearForm() {
        fieldPassword.setStyle("");
        fieldConfirmation.setStyle("");
        fieldConfirmation.setText("");
        fieldPassword.setText("");
    }

    public void setEditable(boolean editable) {
        fieldPassword.setEditable(editable);
        fieldConfirmation.setEditable(editable);
    }


    public boolean isValid() {
        return fieldPassword.getText().equals(fieldConfirmation.getText());
    }
}
