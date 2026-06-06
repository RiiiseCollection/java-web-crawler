package at.cc.main.javawebcrawler.core.engine;

import at.cc.main.javawebcrawler.core.extractor.HtmlExtractor;
import at.cc.main.javawebcrawler.core.fetcher.UrlFetcher;
import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import at.cc.main.javawebcrawler.network.JsoupHttpClient;
import at.cc.main.javawebcrawler.util.UrlUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrawlerEngine {
    private final UrlFetcher urlFetcher;
    private final HtmlExtractor htmlExtractor;
    private final Set<String> visitedUrls;
    private final List<Webpage> crawledPages;
    private final List<String> allowedDomains;
    private final int maxDepth;

    public CrawlerEngine(int maxDepth, List<String> allowedDomains) {
        this(maxDepth, allowedDomains, new UrlFetcher(new JsoupHttpClient()), new HtmlExtractor());
    }

    public CrawlerEngine(int maxDepth, List<String> allowedDomains, UrlFetcher urlFetcher, HtmlExtractor htmlExtractor) {
        this.urlFetcher = urlFetcher;
        this.htmlExtractor = htmlExtractor;
        this.visitedUrls = new HashSet<>();
        this.crawledPages = new ArrayList<>();
        this.allowedDomains = allowedDomains;
        this.maxDepth = maxDepth;
    }

    public void crawl(String startUrl) {
        crawlRecursive(startUrl, 0);
    }

    private void crawlRecursive(String url, int currentDepth) {
        if (currentDepth > maxDepth || visitedUrls.contains(url)) {
            return;
        }

        if (!UrlUtil.isAllowedDomain(url, allowedDomains)) {
            return;
        }

        visitedUrls.add(url);
        System.out.println("Crawling: " + url + " at depth " + currentDepth);

        FetchResult fetchResult = urlFetcher.fetchUrl(url);

        htmlExtractor.extractWebpage(fetchResult, currentDepth).ifPresent(page -> {
            crawledPages.add(page);

            if (fetchResult.isSuccess() && currentDepth < maxDepth) {
                page.links().stream()
                        .filter(link -> !link.isBroken())
                        .forEach(linkObj -> crawlRecursive(linkObj.link(), currentDepth + 1));
            }
        });

    }

    public List<Webpage> getCrawledPages() {
        return crawledPages;
    }

    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }
}