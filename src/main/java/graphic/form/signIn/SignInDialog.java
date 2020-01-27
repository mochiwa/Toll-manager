package graphic.form.signIn;

import graphic.components.popupDialog.PopupDialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.authentication.AuthenticationException;
import tollmanager.model.identity.user.authentication.IAuthenticationService;
import tollmanager.model.identity.user.password.Password;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class SignInDialog extends PopupDialog<User>{
    @FXML protected Label labelError;
    @FXML protected TextField fieldLogin;
    @FXML protected PasswordField fieldPassword;
    @FXML protected ImageView imageView;

    private IAuthenticationService authenticationService;
    private User connectedUser;


    public SignInDialog(IAuthenticationService service) {
        super();
        authenticationService=service;

        loadForm();
        initReturnConverter();

        labelError.setVisible(false);
        fieldPassword.setOnKeyPressed(k->{
            if(k.getCode().equals(KeyCode.ENTER))
                buttonValidate.fire();
        });
        fieldLogin.requestFocus();
    }

    private void loadForm() {
        FXMLLoader loader=new FXMLLoader(SignInDialog.class.getResource("signInForm.fxml"));
        loader.setController(this);
        try {
            docker.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        initStyle(StageStyle.UNDECORATED);
        docker.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/adminRegistrationDialog.css")).toExternalForm());
        imageView.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("image/logoTollManager.gif")).toExternalForm()));
    }


    private void initReturnConverter() {
        setResultConverter(buttonType -> {
            if(buttonType==ButtonType.OK)
                return connectedUser;
            return null;
        });
    }

    @Override
    protected User getUserData() {
        return getUser();
    }


    public User getUser() {
        try {
            return authenticationService.signIn(Login.of(fieldLogin.getText()), Password.of(fieldPassword.getText()));
        }catch (AuthenticationException e) {
            labelError.setText(e.getMessage());
            labelError.setVisible(true);
            fieldLogin.requestFocus();
        }catch (Exception ignored){}
        clearFields();
        return null;
    }
    private void clearFields() {
        fieldLogin.setText("");
        fieldPassword.setText("");
    }


}
