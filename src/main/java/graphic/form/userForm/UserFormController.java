package graphic.form.userForm;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.password.Password;

import java.net.URL;
import java.util.ResourceBundle;

public class UserFormController implements Initializable {
    @FXML private AnchorPane mainPane;
    @FXML private LoginFormController loginFormController;
    @FXML private PasswordFormController passwordFormController;

    public UserFormController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public Login getLogin() {
        return loginFormController.getLogin();
    }

    public Password getPassword(){
        if(passwordFormController.password()!=null)
            return passwordFormController.password().getValue();
        return null;
    }

    public void clearForm() {
        loginFormController.clearForm();
        passwordFormController.clearForm();
    }
    public void setEditable(boolean editable) {
        loginFormController.setEditable(editable);
        passwordFormController.setEditable(editable);
    }

    public boolean isValid() {
        return loginFormController.isValid() && passwordFormController.isValid();
    }

    public void setVisible(boolean value){
        mainPane.setVisible(value);
    }


    public boolean isVisible() {
        return mainPane.isVisible();
    }
}

