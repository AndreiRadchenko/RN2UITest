package unidesign.rn2uitest.SettingsTools;

/**
 * Created by United on 12/26/2017.
 */

public class RestoreRecyclerItem {

    private String name;
    private String comment;
    private String SMS_file_path;
    private String USSD_file_path;

    public RestoreRecyclerItem(String name, String comment, String SMS_file_path, String USSD_file_path) {
        this.name = name;
        this.comment = comment;
        this.SMS_file_path = SMS_file_path;
        this.USSD_file_path = USSD_file_path;
    }

    public RestoreRecyclerItem(RestoreRecyclerItem Item) {
        this.name = Item.name;
        this.comment = Item.comment;
        this.USSD_file_path = Item.USSD_file_path;
        this.SMS_file_path = Item.SMS_file_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSMS_file_path() {
        return SMS_file_path;
    }

    public void setSMS_file_path(String SMS_file_path) {
        this.SMS_file_path = SMS_file_path;
    }

    public String getUSSD_file_path() {
        return USSD_file_path;
    }

    public void setUSSD_file_path(String USSD_file_path) {
        this.USSD_file_path = USSD_file_path;
    }

}
