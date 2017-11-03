package unidesign.rn2uitest.MySQLight;

/**
 * Created by United on 4/4/2017.
 */

public class USSD_Template {
    private long id;
    private String name;
    private String comment;
    private String phone;
    private String template;
    private String image;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String str) {
        this.phone = str;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String str) {
        this.template = str;
    }

    public void setImage(String str) {
        this.image = str;
    }

    public String getImage() {
        return image;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return comment;
    }
}
