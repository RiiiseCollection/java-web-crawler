package at.cc.main.javawebcrawler.core.extractor;

import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.HeaderLevel;
import at.cc.main.javawebcrawler.data.webpage.Headline;
import at.cc.main.javawebcrawler.data.webpage.Link;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Stack;

public class HtmlExtractor {

    public Webpage extractWebpage(FetchResult fetchResult, int currentDepth) {
        Webpage webpage;

        if (fetchResult.isBrokenUrl()) {
            webpage = new Webpage(new Link(fetchResult.getUrl(), true), null, null, currentDepth);
        } else {
            if (fetchResult.getDocument() == null) {
                return null;
            }

            Document doc = fetchResult.getDocument();

            LinkedHashSet<Link> links = extractLinks(doc);
            List<Headline> headlines = extractHeadlines(doc);

            webpage = new Webpage(new Link(fetchResult.getUrl(), false), links, headlines, currentDepth);
        }
        return webpage;
    }

    private LinkedHashSet<Link> extractLinks(Document doc) {
        if (doc == null) {
            return null;
        }

        LinkedHashSet<Link> links = new LinkedHashSet<>();

        Elements docLinks = doc.select("a[href]");
        for (Element link : docLinks) {
            links.add(new Link(link.attr("abs:href"), false));
        }

        return links;
    }

    private List<Headline> extractHeadlines(Document doc) {
        if (doc == null) {
            return null;
        }

        ArrayList<Headline> headlines = new ArrayList<>();

        Elements docHeadlines = doc.select("h1, h2, h3, h4, h5, h6");
        Stack<Headline> headlineStack = new Stack<>();

        for (Element headline : docHeadlines) {
            String text = headline.text();
            HeaderLevel level = HeaderLevel.tagToLevel(headline.tag());

            while (!headlineStack.isEmpty() && headlineStack.peek().getHeaderLevel().getLevel() >= level.getLevel()) {
                headlineStack.pop();
            }

            Headline parent = headlineStack.isEmpty() ? null : headlineStack.peek();
            Headline item = new Headline(level, text, parent);

            headlines.add(item);
            headlineStack.push(item);

            if (parent != null) {
                parent.addChild(item);
            }
        }

        return headlines;
    }

}
