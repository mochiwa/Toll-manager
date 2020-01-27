package graphic.form.email;

import graphic.components.input.FormTextField;
import graphic.form.FormQueryEvent;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import tollmanager.model.identity.contact.Email;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmailFormController implements Initializable {
    @FXML private GridPane mainPane;
    @FXML protected FormTextField fieldEmail;
    private SimpleBooleanProperty isValid;

    public EmailFormController() {
        isValid=new SimpleBooleanProperty();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fieldEmail.setRegex(Email.REGEX);
        fieldEmail.focusedProperty().addListener((observableValue, old, isFocus) -> {
            if(old && !isFocus)
                if( fieldEmail.isCorrectlyFilled())
                    fieldEmail.fireEvent(FormQueryEvent.isEmailUsedEvent(getEmail()));
                else
                    setErrorOnField(fieldEmail,"The email must be like xxxx@xxxx.xxx",!fieldEmail.isCorrectlyFilled());
        });

        fieldEmail.addEventHandler(FormQueryEvent.IS_EMAIL_USED,e->setErrorOnField(fieldEmail,"Email already used",e.getQuery().isUsed()));
        fieldEmail.textProperty().addListener((o) -> isValid.set(fieldEmail.isCorrectlyFilled()));
        mainPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/emailForm.css")).toExternalForm());

    }

    public Email getEmail() {
        try {
            return Email.of(fieldEmail.getText().trim());
        }catch (Exception e) {
            setErrorOnField(fieldEmail,e.getMessage(),true);
        }
        return null;
    }

    public void fillEmail(Email email) {
        fieldEmail.setText(email.value());
    }

    public void setEditable(boolean editable) {
        fieldEmail.setEditable(editable);
    }

    public void clearForm() {
        fieldEmail.resetField();
    }

    public boolean isValid(){
        return isValid.get();
    }


    private void setErrorOnField(FormTextField field,String errorMessage , boolean isError) {
        if (isError)
            field.setError(errorMessage);
        else
            field.setFieldValidate();
        isValid.set(!fieldEmail.isError());
    }
}
