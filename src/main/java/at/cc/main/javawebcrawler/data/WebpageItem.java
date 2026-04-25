package at.cc.main.javawebcrawler.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WebpageItem {
    private final LinkItem root;
    private final HashSet<LinkItem> links;
    private final ArrayList<HeadlineItem> headlines;
    private final int depth;

    public WebpageItem (LinkItem root, HashSet<LinkItem> links, ArrayList<HeadlineItem> headlines, int depth) {
        this.root = root;
        this.links = links;
        this.headlines = headlines;
        this.depth = depth;
    }

    public LinkItem getRoot() {
        return root;
    }

    public HashSet<LinkItem> getLinks() {
        return links;
    }

    public List<HeadlineItem> getHeadlines() {
        return headlines;
    }

    public int getDepth() {
        return depth;
    }
}
