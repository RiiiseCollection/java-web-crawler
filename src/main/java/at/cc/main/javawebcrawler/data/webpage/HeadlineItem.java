package at.cc.main.javawebcrawler.data.webpage;

import java.util.ArrayList;
import java.util.List;

public class HeadlineItem {
    private final HeaderLevel headerLevel;
    private final String text;
    private final HeadlineItem parent;
    private final ArrayList<HeadlineItem> children = new ArrayList<>();

    public HeadlineItem(HeaderLevel headerlevel, String text, HeadlineItem parent) {
        this.headerLevel = headerlevel;
        this.text = text;
        this.parent = parent;
    }

    public HeaderLevel getHeaderLevel() {
        return headerLevel;
    }

    public String getText() {
        return text;
    }

    public HeadlineItem getParent() {
        return parent;
    }

    public List<HeadlineItem> getChildren() {
        return children;
    }

    public void addChild(HeadlineItem child) {
        if (child != null) children.add(child);
    }

    public boolean isRoot() {
        return parent == null;
    }
}
