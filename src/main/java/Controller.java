import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ResourceBundle;
import java.util.Set;


public class Controller implements Initializable {

    @FXML
    private Button priceLocationPicker;

    @FXML
    private Button dataLocationPicker;

    @FXML
    private TextField pricePath;

    @FXML
    private TextField dataPath;

    @FXML
    private Button run;

    @FXML
    private Button close;

    @FXML
    private Button detectItem;

    @FXML
    private TextField itemCount;

    @FXML
    private ListView ruleList;

    @FXML
    private CheckBox isWinter;

    private FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Excel 2007+ 文档 (*.xlsx)", "*.xlsx");
    private FileChooser.ExtensionFilter xlsFilter = new FileChooser.ExtensionFilter("Excel 97-2003 文档 (*.xls)", "*.xls");
    private DecimalFormat dFormat = new DecimalFormat("#.0");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pricePath.setPromptText("请选择文件...");
        dataPath.setPromptText("请选择文件...");
        // pricePath.setText("C:\\ExcelOperate\\price.xlsx");
        // dataPath.setText("C:\\ExcelOperate\\data.xlsx");
        itemCount.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().isEmpty()) {
                return change;
            }

            ParsePosition parsePosition = new ParsePosition(0);
            Object object = dFormat.parse(change.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < change.getControlNewText().length()) {
                return null;
            } else {
                return change;
            }
        }));


    }


    public void pickPricePath(ActionEvent event) {
        System.out.println("picking price path");

        Node eventNode = (Node) event.getSource();
        File price;
        FileChooser priceFC = new FileChooser();
        priceFC.setTitle("打开价目单：");
        priceFC.getExtensionFilters().addAll(xlsxFilter, xlsFilter);
        price = priceFC.showOpenDialog(eventNode.getScene().getWindow());
        pricePath.setText(price.getAbsolutePath());

        log(price.getAbsolutePath());
    }

    public void pickDataPath(ActionEvent event) {
        System.out.println("picking data path");

        Node eventNode = (Node) event.getSource();
        File data;
        FileChooser dataFC = new FileChooser();
        dataFC.setTitle("打开运货表：");
        dataFC.getExtensionFilters().addAll(xlsxFilter, xlsFilter);
        data = dataFC.showOpenDialog(eventNode.getScene().getWindow());
        dataPath.setText(data.getAbsolutePath());

        log(data.getAbsolutePath());
    }

    public void execute(ActionEvent event) {
        System.out.println("executing");

        Calculator c = new Calculator();
        if (pricePath.getText().equals("") || dataPath.getText().equals("") || itemCount.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "请选择文件和输入处理条目数！", ButtonType.OK);
            alert.show();
        } else {
            c.calculate(pricePath.getText(), dataPath.getText(), isWinter.isSelected(), Integer.parseInt(itemCount.getText()));
        }
    }

    public void closeProgram(ActionEvent event) {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    public void detectItemCount(ActionEvent event) {
        Calculator c = new Calculator();
        if (dataPath.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "请选择文件！", ButtonType.OK);
            alert.show();
        } else {
            int rowCount = c.detectInfoCount(dataPath.getText());
            itemCount.setText(Integer.toString(rowCount));
        }
    }

    public void log(String s) {
        System.out.println(s);
    }

}