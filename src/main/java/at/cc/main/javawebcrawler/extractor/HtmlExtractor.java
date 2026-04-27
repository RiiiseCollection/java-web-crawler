package at.cc.main.javawebcrawler.extractor;

import at.cc.main.javawebcrawler.data.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Stack;

public class HtmlExtractor {

    public WebpageItem extractWebpage(FetchResult fetchResult, int currentDepth) {
        WebpageItem webpageItem;

        if (fetchResult.isBrokenUrl()) {
            webpageItem = new WebpageItem(new LinkItem(fetchResult.getUrl(), true), null, null, currentDepth);
        } else {
            if (fetchResult.getDocument() == null) {
                return null;
            }

            LinkedHashSet<LinkItem> links = new LinkedHashSet<>();
            ArrayList<HeadlineItem> headlines = new ArrayList<>();

            Document doc = fetchResult.getDocument();

            Elements docLinks = doc.select("a[href]");
            for (Element link : docLinks) {
                links.add(new LinkItem(link.attr("abs:href"), false));
            }

            Elements docHeadlines = doc.select("h1, h2, h3, h4, h5, h6");
            Stack<HeadlineItem> headlineItemStack = new Stack<>();

            for (Element headline : docHeadlines) {
                String text = headline.text();
                HeaderLevel level = HeaderLevel.tagToLevel(headline.tag());

                while (!headlineItemStack.isEmpty() && headlineItemStack.peek().getHeaderLevel().getLevel() >= level.getLevel()) {
                    headlineItemStack.pop();
                }

                HeadlineItem parent = headlineItemStack.isEmpty() ? null : headlineItemStack.peek();
                HeadlineItem item = new HeadlineItem(level, text, parent);

                headlines.add(item);
                headlineItemStack.push(item);

                if(parent != null) {
                    parent.addChild(item);
                }
            }

            webpageItem = new WebpageItem(new LinkItem(fetchResult.getUrl(), false), links, headlines, currentDepth);
        }
        return webpageItem;
    }

}
