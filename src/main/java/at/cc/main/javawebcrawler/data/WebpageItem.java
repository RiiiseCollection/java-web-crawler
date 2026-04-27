package at.cc.main.javawebcrawler.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebpageItem {
    private final LinkItem root;
    private final Set<LinkItem> links;
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

    public Set<LinkItem> getLinks() {
        return links;
    }

    public List<HeadlineItem> getHeadlines() {
        return headlines;
    }

    public int getDepth() {
        return depth;
    }
}
