package graphic.form.person;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class BirthdayTextFormatter implements UnaryOperator<TextFormatter.Change> {
    private final String dateRegex="\\d{4}/\\d{2}/\\d{2}";

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        String input = change.getText();
        String currentText=change.getControlText();
        String newText=change.getControlNewText();

        if(change.isDeleted())
            return change;


        if(!isNumeric(input) || (currentText.matches(dateRegex) &&  !input.isEmpty() ))
            return null;

        if(newText.length()==4)
            insertCharacters(change,"/");
        else if(newText.length()==7)
            insertCharacters(change,"/");


        return change;
    }

    private boolean isNumeric(String input) {
        return input.matches("\\d")  || input.isEmpty();
    }

    private void insertCharacters(TextFormatter.Change change, String characters) {
        change.setText(change.getText()+characters);
        change.setCaretPosition(change.getControlNewText().length());
        change.setAnchor(change.getControlNewText().length());

    }

}
