package at.cc.main.javawebcrawler.engine;

import at.cc.main.javawebcrawler.data.WebpageItem;
import at.cc.main.javawebcrawler.extractor.HtmlExtractor;
import at.cc.main.javawebcrawler.fetcher.UrlFetcher;
import at.cc.main.javawebcrawler.network.JsoupHttpClient;

import java.util.*;

public class CrawlerEngine {
    private final UrlFetcher urlFetcher;
    private final HtmlExtractor htmlExtractor;
    private final Set<String> visitedUrls;
    private final List<WebpageItem> crawledPages;
    private final int maxDepth;

    public CrawlerEngine(int maxDepth) {
        this.urlFetcher = new UrlFetcher(new JsoupHttpClient());
        this.htmlExtractor = new HtmlExtractor();
        this.visitedUrls = new HashSet<>();
        this.crawledPages = new ArrayList<>();
        this.maxDepth = maxDepth;
    }

    public void crawl(String startUrl) {

    }

    public List<WebpageItem> getCrawledPages() {
        return crawledPages;
    }

    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }
}