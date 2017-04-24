package unidesign.rn2uitest;

/**
 * Created by United on 2/25/2017.
 */

public class RecyclerItem {

    private Long id;
    private String title;
    private String description;
    private String template;

    public RecyclerItem(Long id, String title, String description, String template) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.template = template;
    }
    public Long getID() {
        return id;
    }

    public void setID(Long _id) {
        this.id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String str) {
        this.template = str;
    }
}