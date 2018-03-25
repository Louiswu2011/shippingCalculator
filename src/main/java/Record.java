import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Record{
    private SimpleIntegerProperty ruleNumber;
    private SimpleStringProperty ruleDetail;
    private SimpleStringProperty ruleArea;

    Record(int n, String detail, String area){
        this.ruleNumber = new SimpleIntegerProperty(n);
        this.ruleDetail = new SimpleStringProperty(detail);
        this.ruleArea = new SimpleStringProperty(area);
    }

    public int getRuleNumber(){
        return ruleNumber.get();
    }

    public String getRuleDetail(){
        return ruleDetail.get();
    }

    public String getRuleArea(){
        return ruleArea.get();
    }

}