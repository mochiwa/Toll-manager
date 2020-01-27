package graphic.form.phone;

import javafx.scene.control.TextFormatter;

import java.util.Arrays;
import java.util.function.UnaryOperator;

public class PhoneTextFormatter  implements UnaryOperator<TextFormatter.Change> {
    private final String dotSeparator="[.]";
    private final String firstPartRegex="^\\d{4}$";
    private final String pairPartRegex="^\\d{2}$";
    private final String phoneRegex="^\\d{4}\\.\\d{2}\\.\\d{2}\\.\\d{2}$";


    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        String input = change.getText();
        String currentText=change.getControlText();
        String newText=change.getControlNewText();

        if(change.isDeleted())
            return change;

        if (!isNumeric(input) || (isLengthEnough(currentText) && !input.isEmpty()))
            return null;


        if (shouldInsertPoint(newText))
            insertCharacters(change,".");
        return change;
    }

    private boolean isLengthEnough(String text) {
        int maxLength=13;
        return text.length()>=maxLength;
    }

    private boolean isNumeric(String input) {
        return input.matches("\\d") || input.isEmpty();
    }

    private void insertCharacters(TextFormatter.Change change, String characters) {
        change.setText(change.getText()+characters);
        change.setCaretPosition(change.getControlNewText().length());
        change.setAnchor(change.getControlNewText().length());
    }


    private boolean shouldInsertPoint(String actualText) {
        if(actualText.isEmpty() || actualText.matches(phoneRegex))
            return false;

        if(actualText.matches(firstPartRegex) && actualText.length()==4)
            return true;


        if(actualText.length()<5)
            return false;

        String[] data=actualText.split("[.]");
        return data[data.length-1].matches(pairPartRegex);
    }
}
