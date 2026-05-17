package at.cc.main.javawebcrawler.data.webpage;

import java.util.ArrayList;
import java.util.List;

public class Headline {
    private final HeaderLevel headerLevel;
    private final String text;
    private final Headline parent;
    private final ArrayList<Headline> children = new ArrayList<>();

    public Headline(HeaderLevel headerlevel, String text, Headline parent) {
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

    public Headline getParent() {
        return parent;
    }

    public List<Headline> getChildren() {
        return children;
    }

    public void addChild(Headline child) {
        if (child != null) children.add(child);
    }

    public boolean isRoot() {
        return parent == null;
    }
}
