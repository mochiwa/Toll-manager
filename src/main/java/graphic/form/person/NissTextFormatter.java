package graphic.form.person;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.function.UnaryOperator;


public class NissTextFormatter implements UnaryOperator<TextFormatter.Change>{
    private final String dotSeparator="[.]";
    private final String hypen="-";

    private final String birthdayPartRegex="^\\d{2}\\.\\d{2}\\.\\d{2}";
    private final String birthCountRegex="-\\d{3}";

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        String input = change.getText();
        String currentText=change.getControlText();
        String newText=change.getControlNewText();

        if(change.isDeleted())
            return change;

        if (!isNumeric(input) || (isLengthEnough(currentText) &&  !input.isEmpty()))
            return null;

        if (shouldInsertPoint(newText))
            insertCharacters(change,".");
        if(shouldInsertHypen(newText))
            insertCharacters(change,"-");
        return change;
    }

    private boolean isLengthEnough(String text) {
        int maxLength=15;
        return text.length()>=maxLength;
    }

    private boolean isNumeric(String input) {
        return input.matches("\\d")|| input.isEmpty();
    }

    private void insertCharacters(TextFormatter.Change change, String characters) {
        change.setText(change.getText()+characters);
        change.setCaretPosition(change.getControlNewText().length());
        change.setAnchor(change.getControlNewText().length());
    }



    private boolean shouldInsertPoint(String actualText) {
        if(actualText.isEmpty() || actualText.matches(birthdayPartRegex))
            return false;

        if(actualText.contains(hypen))
            return actualText.matches(birthdayPartRegex + birthCountRegex);

        return Arrays.toString(actualText.split(dotSeparator)).length()%2==0;
    }

    private boolean shouldInsertHypen(String controlNewText) {
        if(controlNewText.matches(birthdayPartRegex))
            return !controlNewText.matches(birthCountRegex);
        return false;
    }
}
