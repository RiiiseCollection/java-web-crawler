package at.cc.main.javawebcrawler.core.extractor;

import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.HeaderLevel;
import at.cc.main.javawebcrawler.data.webpage.Headline;
import at.cc.main.javawebcrawler.data.webpage.Link;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class HtmlExtractor {

    public Optional<Webpage> extractWebpage(FetchResult fetchResult, int currentDepth) {
        if (fetchResult.isBrokenUrl()) {
            return Optional.of(new Webpage(new Link(fetchResult.getUrl(), true), Collections.emptySet(), Collections.emptyList(), currentDepth));
        }

        if (fetchResult.getBody() == null) {
            return Optional.empty();
        }

        Document doc = Jsoup.parse(fetchResult.getBody(), fetchResult.getUrl());
        LinkedHashSet<Link> links = extractLinks(doc);
        List<Headline> headlines = extractHeadlines(doc);

        return Optional.of(new Webpage(new Link(fetchResult.getUrl(), false), links, headlines, currentDepth));
    }

    private LinkedHashSet<Link> extractLinks(Document doc) {
        LinkedHashSet<Link> links = new LinkedHashSet<>();

        Elements docLinks = doc.select("a[href]");
        for (Element link : docLinks) {
            links.add(new Link(link.attr("abs:href"), false));
        }

        return links;
    }

    private List<Headline> extractHeadlines(Document doc) {
        ArrayList<Headline> headlines = new ArrayList<>();

        Elements docHeadlines = doc.select("h1, h2, h3, h4, h5, h6");
        Stack<Headline> headlineStack = new Stack<>();

        for (Element headline : docHeadlines) {
            HeaderLevel.tagToLevel(headline.tag()).ifPresent(level -> {
                String text = headline.text();

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
            });
        }
        return headlines;
    }

}
