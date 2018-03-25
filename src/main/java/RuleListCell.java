import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class RuleListCell extends TableCell<Record, Double> {
    private TextField textField;

    public RuleListCell(){}

    @Override
    public void startEdit() {
        super.startEdit();

        if(textField == null){

        }
    }
}
