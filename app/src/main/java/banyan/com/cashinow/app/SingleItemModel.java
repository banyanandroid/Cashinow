package banyan.com.cashinow.app;

/**
 * Created by Jarvis on 21-05-2018.
 */
public class SingleItemModel {


    private String event_id;
    private String event_type;
    private String event_no;
    private String message;
    private String event_name;
    private String event_date;
    private String event_amount;

    public SingleItemModel() {
    }

    public SingleItemModel(String event_id, String event_type, String event_no, String message, String event_name,
                           String event_date, String event_amount) {
        this.event_id = event_id;
        this.event_type = event_type;
        this.event_no = event_no;
        this.message = message;
        this.event_name = event_name;
        this.event_date = event_date;
        this.event_amount = event_amount;
    }

    public String getevent_id() {
        return event_id;
    }

    public void setevent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getevent_type() {
        return event_type;
    }

    public void setevent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getevent_no() {
        return event_no;
    }

    public void setevent_no(String event_no) {
        this.event_type = event_no;
    }

    public String getevent_name() {
        return event_name;
    }

    public void setevent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getmessage() {
        return message;
    }

    public void setName(String message) {
        this.message = message;
    }

    public String getevent_date() {
        return event_date;
    }

    public void setevent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getevent_amount() {
        return event_amount;
    }

    public void setevent_amount(String event_amount) {
        this.event_amount = event_amount;
    }


}
