package at.cc.main.javawebcrawler.data;

public class HeadlineItem {
    private final String tag;
    private final String text;

    public HeadlineItem(String tag, String text) {
        this.tag = tag;
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public String getText() {
        return text;
    }
}
