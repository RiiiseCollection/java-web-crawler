package at.cc.main.javawebcrawler.core.engine;

import at.cc.main.javawebcrawler.core.engine.CrawlerEngine;
import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.LinkItem;
import at.cc.main.javawebcrawler.data.webpage.WebpageItem;
import at.cc.main.javawebcrawler.core.extractor.HtmlExtractor;
import at.cc.main.javawebcrawler.core.fetcher.UrlFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CrawlerEngineTest {

    private UrlFetcher urlFetcher;
    private HtmlExtractor htmlExtractor;
    private CrawlerEngine crawlerEngine;
    private List<String> domains;
    private String url = "https://aau.at";

    @BeforeEach
    void setUp() {
        urlFetcher = mock(UrlFetcher.class);
        htmlExtractor = mock(HtmlExtractor.class);
        domains = new ArrayList<>();
        domains.add("aau.at");
        crawlerEngine = new CrawlerEngine(2, domains, urlFetcher, htmlExtractor);
    }

    @Test
    void crawlSinglePageSuccessfully() {
        FetchResult fetchResult = mock(FetchResult.class);
        when(fetchResult.isSuccess()).thenReturn(true);
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        WebpageItem webpageItem = new WebpageItem(
                new LinkItem(url, false),
                new LinkedHashSet<>(),
                new ArrayList<>(),
                0
        );
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(webpageItem);

        crawlerEngine.crawl(url);

        assertEquals(1, crawlerEngine.getVisitedUrls().size());
        assertTrue(crawlerEngine.getVisitedUrls().contains(url));
    }

    @Test
    void crawlUpToMaxDepth() {
        String deepUrl = "https://aau.at/deep";
        FetchResult fetchResult = mock(FetchResult.class);
        when(fetchResult.isSuccess()).thenReturn(true);
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<LinkItem> links = new LinkedHashSet<>();
        links.add(new LinkItem(deepUrl, false));

        WebpageItem webpageItem = new WebpageItem(
                new LinkItem(url, false), links, new ArrayList<>(), 0
        );
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(webpageItem);

        crawlerEngine = new CrawlerEngine(0, domains, urlFetcher, htmlExtractor);

        crawlerEngine.crawl(url);

        assertEquals(1, crawlerEngine.getVisitedUrls().size());
    }

    @Test
    void crawlUrlsOnce() {
        FetchResult fetchResult = mock(FetchResult.class);
        when(fetchResult.isSuccess()).thenReturn(true);
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<LinkItem> links = new LinkedHashSet<>();
        links.add(new LinkItem(url, false));

        WebpageItem webpageItem = new WebpageItem(
                new LinkItem(url, false), links, new ArrayList<>(), 0
        );
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(webpageItem);

        crawlerEngine.crawl(url);

        assertEquals(1, crawlerEngine.getVisitedUrls().size());
    }

    @Test
    void handleBrokenLinks() {
        String brokenUrl = "https://aau.at/broken";
        FetchResult fetchResult = mock(FetchResult.class);
        when(fetchResult.isSuccess()).thenReturn(false);
        when(fetchResult.getUrl()).thenReturn(brokenUrl);
        when(urlFetcher.fetchUrl(brokenUrl)).thenReturn(fetchResult);

        WebpageItem webpageItem = new WebpageItem(
                new LinkItem(brokenUrl, true), null, null, 0
        );
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(webpageItem);

        crawlerEngine.crawl(brokenUrl);

        assertEquals(1, crawlerEngine.getCrawledPages().size());
        assertTrue(crawlerEngine.getCrawledPages().getFirst().getRoot().isBroken());
    }

    @Test
    void initiallyReturnEmptyList() {
        assertTrue(crawlerEngine.getCrawledPages().isEmpty());
        assertTrue(crawlerEngine.getVisitedUrls().isEmpty());
    }
}