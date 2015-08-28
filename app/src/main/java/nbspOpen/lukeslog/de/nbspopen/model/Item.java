package nbspOpen.lukeslog.de.nbspopen.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="item")
public class Item {

    @Element(required = false)
    String title;

    @Element(required = false)
    String link;

    @Element(required = false)
    String guid;

    @Element(required = false)
    String pubDate;

    public Item() {
        super();
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
