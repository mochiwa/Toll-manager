package graphic.form.adminRegister;

import graphic.components.popupDialog.PopupDialog;
import graphic.form.person.PersonFormController;
import graphic.form.userForm.PasswordFormController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.user.password.Password;

import java.util.Objects;

public class AdminRegistrationDialog extends PopupDialog<Person>{
    @FXML private PersonFormController personFormController;
    @FXML private PasswordFormController passwordFormController;
    @FXML private ImageView imageView;
    private Person administrator;

    public AdminRegistrationDialog() {
        super();
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(AdminRegistrationDialog.class.getResource("adminRegistrationDialog.fxml"));

        try {
            loader.setController(this);
            docker.getChildren().add(loader.load());
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("image/logoTollManager.gif")).toExternalForm()));
            docker.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/adminRegistrationDialog.css")).toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initStyle(StageStyle.UNDECORATED);
        setTitle("Administrator register");
        initModality(Modality.APPLICATION_MODAL);

        getDialogPane().addEventFilter(ActionEvent.ACTION, event -> {
            if(event.getTarget().equals(buttonCancel) && !confirmExit())
                    event.consume();
        });
    }

    public boolean confirmExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit confirmation");
        alert.setHeaderText("administrator needed !");
        alert.setContentText("If you don't register an administrator the application will be closed !");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        return  alert.showAndWait().filter(response->response.equals(ButtonType.OK)).isPresent();
    }

    @Override
    protected Person getUserData() {
       Person person=personFormController.getPerson();
       if(passwordFormController.password()==null)
           return null;
       return person;
    }

    public Pair<Password,Password> getPasswordAndConfirmation() {
        return passwordFormController.password();
    }
}
