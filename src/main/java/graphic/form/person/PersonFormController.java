package graphic.form.person;

import graphic.components.input.FormTextField;
import graphic.form.FormQueryEvent;
import graphic.form.address.AddressFormController;
import graphic.form.email.EmailFormController;
import graphic.form.phone.PhoneFormController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tollmanager.model.identity.contact.*;
import tollmanager.model.identity.person.Birthday;
import tollmanager.model.identity.person.FullName;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.person.Person;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class PersonFormController implements Initializable {
    @FXML private VBox mainLayout;
    @FXML protected AddressFormController addressFormController;
    @FXML protected EmailFormController emailFormController;
    @FXML protected PhoneFormController phoneFormController;

    @FXML protected FormTextField fieldName;
    @FXML protected FormTextField fieldForename;
    @FXML protected FormTextField fieldNiss;
    @FXML protected FormTextField fieldBirthday;
    @FXML protected GridPane personLayout;


    private SimpleBooleanProperty isValidProperty;

    public PersonFormController(){
        initIsValidProperty();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initNissField();
        initBirthdayField();

        mainLayout.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/employeeForm.css")).toExternalForm());

        fieldName.focusedProperty().addListener((observableValue, old, isFocus) -> {
            if(old && !isFocus)
                setErrorOnField(fieldName,"The field is required.",fieldName.getText().isEmpty());
        });

        fieldForename.focusedProperty().addListener((observableValue, old, isFocus) -> {
            if(old && !isFocus)
                setErrorOnField(fieldForename,"The field is required.",fieldForename.getText().isEmpty());
        });

    }

    private void initIsValidProperty(){
        isValidProperty =new SimpleBooleanProperty(true);
    }

    private void initNissField(){
        fieldNiss.setTextFormatter(new TextFormatter<String>(new NissTextFormatter()));
        fieldNiss.setRegex(Niss.REGEX);

        fieldNiss.focusedProperty().addListener((observableValue, old, isFocus) -> {
            if((old && !isFocus))
                if(fieldNiss.isCorrectlyFilled())
                    fieldNiss.fireEvent(FormQueryEvent.isNissUsedEvent(Niss.of(fieldNiss.getText())));
                else
                    setErrorOnField(fieldNiss,"The field must be like xx.xx.xx-xxx.xx",!fieldNiss.isCorrectlyFilled());
        });

        fieldNiss.addEventHandler(FormQueryEvent.IS_NISS_USED,e->{ setErrorOnField(fieldNiss,"The niss is already used",e.getQuery().isUsed());});
    }

    private void initBirthdayField(){
        fieldBirthday.setTextFormatter(new TextFormatter<String>(new BirthdayTextFormatter()));
        fieldBirthday.setRegex(Birthday.US_YEARS_REGEX);

        fieldBirthday.focusedProperty().addListener((observableValue, old, isFocus) -> {
            if(old && !isFocus) {
                setErrorOnField(fieldBirthday, "The field must be like yyyy/mm/dd.", !fieldBirthday.isCorrectlyFilled());
            }
        });
    }


    public Person getPerson() {
        try{

            LinkedHashSet<PostalAddress> addresses=new LinkedHashSet<>();
            LinkedHashSet<Email> emails=new LinkedHashSet<>();
            LinkedHashSet<Phone> phones=new LinkedHashSet<>();



            PostalAddress address=addressFormController.getAddress();
            Email mail=emailFormController.getEmail();
            Phone phone=phoneFormController.getPhone();

            if(address!=null)
                addresses.add(address);
            if(mail!=null)
                emails.add(mail);
            if(phone!=null)
                phones.add(phone);

            Person p=Person.of(
                    Niss.of(fieldNiss.getText()),
                    FullName.of(fieldName.getText(), fieldForename.getText()),
                    Birthday.of(fieldBirthday.getText()),
                    ContactInformationBuilder.of()
                            .setAddresses(addresses)
                            .setPhones(phones)
                            .setEmails(emails)
                            .create());

            if(!emailFormController.isValid() || !phoneFormController.isValid() ||fieldNiss.isError() || !addressFormController.isValid())
                throw new IllegalArgumentException("data already used !");
            return p;
        }catch (Exception e){
            e.printStackTrace();
            personLayout.getChildren().forEach(node -> {
                if(node instanceof FormTextField){
                    FormTextField field=(FormTextField) node;
                    if(field.getText().isEmpty())
                        field.requestFocus();
                }
            });
            mainLayout.requestFocus();
        }
        return null;
    }

    public void fillPerson(Person person) {
        addressFormController.fillAddress(person.contactInformation().getAddress(0));
        phoneFormController.fillPhone(person.contactInformation().getPhone(0));
        emailFormController.fillEmail(person.contactInformation().getEmail(0));

        fieldName.setText(person.fullName().name());
        fieldForename.setText(person.fullName().forename());
        fieldNiss.setTextFormatter(null);
        fieldNiss.setText(person.niss().value());
        fieldNiss.setTextFormatter(new TextFormatter<String>(new NissTextFormatter()));

        fieldBirthday.setTextFormatter(null);
        fieldBirthday.setText(person.birthday().value());
        fieldBirthday.setTextFormatter(new TextFormatter<String>(new BirthdayTextFormatter()));
    }

    public void setEditable(boolean editable) {
        addressFormController.setEditable(editable);
        phoneFormController.setEditable(editable);
        emailFormController.setEditable(editable);

        fieldBirthday.setEditable(editable);
        fieldNiss.setEditable(editable);
        fieldName.setEditable(editable);
        fieldForename.setEditable(editable);
        isValidProperty.set(true);
    }

    public void clearForm() {
        fieldName.resetField();
        fieldForename.resetField();
        fieldNiss.resetField();
        fieldBirthday.resetField();

        addressFormController.clearForm();
        phoneFormController.clearForm();
        emailFormController.clearForm();
    }


    private void setErrorOnField(FormTextField field,String errorMessage , boolean isError) {
        if (isError)
            field.setError(errorMessage);
        else
            field.setFieldValidate();

        isValidProperty.set(!(fieldName.isError() ||
                fieldForename.isError() ||
                fieldBirthday.isError() ||
                fieldNiss.isError() ||
                !addressFormController.isValid()
                ));
    }

    public SimpleBooleanProperty isValidProperty() {
        return isValidProperty;
    }
}
