package unidesign.rn2uitest.ImportActivity;

/**
 * Created by United on 9/13/2017.
 */

public class ImportRecyclerItem {

    private String name;
    private String templatename;
    private String jsondirref;
    private String pngdirref;
    private String templatetype;

    public ImportRecyclerItem(String name, String templatename, String jsondirref, String pngdirref, String templatetype) {
        this.name = name;
        this.templatename = templatename;
        this.jsondirref = jsondirref;
        this.pngdirref = pngdirref;
        this.templatetype = templatetype;
    }

    public ImportRecyclerItem(ImportRecyclerItem Item) {
        this.name = Item.name;
        this.templatename = Item.templatename;
        this.jsondirref = Item.jsondirref;
        this.pngdirref = Item.pngdirref;
        this.templatetype = Item.templatetype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplatename() {
        return templatename;
    }

    public void setTemplatename(String templatename) {
        this.templatename = templatename;
    }

    public String getJsondirref() {
        return jsondirref;
    }

    public void setJsondirref(String jsondirref) {
        this.jsondirref = jsondirref;
    }

    public String getPngdirref() {
        return pngdirref;
    }

    public void setPngdirref(String pngdirref) {
        this.pngdirref = pngdirref;
    }

    public String getTemplatetype() {
        return templatetype;
    }

    public void setTemplatetype(String templatetype) {
        this.templatetype = templatetype;
    }
}

