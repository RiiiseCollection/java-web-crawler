package at.cc.main.javawebcrawler.extractor;

import at.cc.main.javawebcrawler.data.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;

public class HtmlExtractor {

    public WebpageItem extractWebpage(FetchResult fetchResult, int currentDepth) {
        WebpageItem webpageItem;

        if (fetchResult.isBrokenUrl()) {
            webpageItem = new WebpageItem(new LinkItem(fetchResult.getUrl(), true), null, null, currentDepth);
        } else {
            if(fetchResult.getDocument() == null) return null;

            HashSet<LinkItem> links = new HashSet<>();
            ArrayList<HeadlineItem> headlines = new ArrayList<>();

            Document doc = fetchResult.getDocument();

            Elements docLinks = doc.select("a[href]");
            for (Element link : docLinks) {
                links.add(new LinkItem(link.attr("abs:href"), false));
            }

            Elements docHeadlines = doc.select("h1, h2, h3, h4, h5, h6");
            for (Element headline : docHeadlines) {
                headlines.add(new HeadlineItem(HeaderLevel.tagToLevel(headline.tag()), headline.text()));
            }

            webpageItem = new WebpageItem(new LinkItem(fetchResult.getUrl(), false), links, headlines, currentDepth);
        }
        return webpageItem;
    }

}
