package at.cc.main.javawebcrawler.core.engine;

import at.cc.main.javawebcrawler.core.extractor.HtmlExtractor;
import at.cc.main.javawebcrawler.core.fetcher.UrlFetcher;
import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.Link;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CrawlerEngineTest {

    private UrlFetcher urlFetcher;
    private HtmlExtractor htmlExtractor;
    private CrawlerEngine crawlerEngine;
    private List<String> domains;
    private final String url = "https://aau.at";

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
        FetchResult fetchResult = successfulFetch(url);
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        Webpage webpage = new Webpage(
                new Link(url, false),
                new LinkedHashSet<>(),
                new ArrayList<>(),
                0
        );
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(webpage));

        crawlerEngine.crawl(url);

        assertEquals(1, crawlerEngine.getVisitedUrls().size());
        assertTrue(crawlerEngine.getVisitedUrls().contains(url));
    }

    @Test
    void crawlUpToMaxDepth() {
        String deepUrl = "https://aau.at/deep";
        FetchResult fetchResult = successfulFetch(url);
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link(deepUrl, false));

        Webpage webpage = new Webpage(
                new Link(url, false), links, new ArrayList<>(), 0
        );
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(webpage));

        crawlerEngine = new CrawlerEngine(0, domains, urlFetcher, htmlExtractor);

        crawlerEngine.crawl(url);

        assertEquals(1, crawlerEngine.getVisitedUrls().size());
    }

    @Test
    void crawlUrlsOnce() {
        FetchResult fetchResult = successfulFetch(url);

        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link(url, false));

        Webpage webpage = new Webpage(
                new Link(url, false), links, new ArrayList<>(), 0
        );
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(webpage));

        crawlerEngine.crawl(url);

        assertEquals(1, crawlerEngine.getVisitedUrls().size());
    }

    @Test
    void doesNotCrawlBeyondMaxDepth() {
        String deepUrl = "https://aau.at/deep";

        FetchResult fetchResult = successfulFetch(url);
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link(deepUrl, false));
        Webpage webpage = new Webpage(new Link(url, false), links, new ArrayList<>(), 0);
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(webpage));

        // maxDepth = 0 → only the start URL may be visited
        crawlerEngine = new CrawlerEngine(0, domains, urlFetcher, htmlExtractor);
        crawlerEngine.crawl(url);

        assertEquals(1, crawlerEngine.getVisitedUrls().size());
        assertFalse(crawlerEngine.getVisitedUrls().contains(deepUrl));
    }

    @Test
    void handleBrokenLinks() {
        String brokenUrl = "https://aau.at/broken";
        FetchResult fetchResult = mock(FetchResult.class);
        when(fetchResult.isSuccess()).thenReturn(false);
        when(fetchResult.getUrl()).thenReturn(brokenUrl);
        when(urlFetcher.fetchUrl(brokenUrl)).thenReturn(fetchResult);

        Webpage webpage = new Webpage(
                new Link(brokenUrl, true), null, null, 0
        );
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(webpage));

        crawlerEngine.crawl(brokenUrl);

        assertEquals(1, crawlerEngine.getCrawledPages().size());
        assertTrue(crawlerEngine.getCrawledPages().getFirst().root().isBroken());
    }

    @Test
    void initiallyHasNoCrawledPages() {
        assertTrue(crawlerEngine.getCrawledPages().isEmpty());
    }

    @Test
    void initiallyHasNoVisitedUrls() {
        assertTrue(crawlerEngine.getVisitedUrls().isEmpty());
    }

    @Test
    void crawlsAllSiblingUrlsConcurrently() {
        String child1 = "https://aau.at/a";
        String child2 = "https://aau.at/b";
        String child3 = "https://aau.at/c";

        FetchResult fetchResult = successfulFetch(url);
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link(child1, false));
        links.add(new Link(child2, false));
        links.add(new Link(child3, false));
        Webpage rootPage = new Webpage(new Link(url, false), links, new ArrayList<>(), 0);
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(rootPage));

        for (String child : List.of(child1, child2, child3)) {
            FetchResult cf = successfulFetch(child);
            when(urlFetcher.fetchUrl(child)).thenReturn(cf);
            when(htmlExtractor.extractWebpage(cf, 1)).thenReturn(Optional.of(emptyPage(child)));
        }

        crawlerEngine = new CrawlerEngine(1, domains, urlFetcher, htmlExtractor);
        crawlerEngine.crawl(url);

        assertEquals(4, crawlerEngine.getVisitedUrls().size());
        assertTrue(crawlerEngine.getVisitedUrls().containsAll(List.of(url, child1, child2, child3)));
    }

    @Test
    void crawlsMultiplePagesAndCollectsAll() {
        String child1 = "https://aau.at/a";
        String child2 = "https://aau.at/b";

        FetchResult fetchResult = successfulFetch(url);
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link(child1, false));
        links.add(new Link(child2, false));
        Webpage rootPage = new Webpage(new Link(url, false), links, new ArrayList<>(), 0);
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(rootPage));

        for (String child : List.of(child1, child2)) {
            FetchResult cf = successfulFetch(child);
            when(urlFetcher.fetchUrl(child)).thenReturn(cf);
            when(htmlExtractor.extractWebpage(cf, 1)).thenReturn(Optional.of(emptyPage(child)));
        }

        crawlerEngine = new CrawlerEngine(1, domains, urlFetcher, htmlExtractor);
        crawlerEngine.crawl(url);

        assertEquals(3, crawlerEngine.getCrawledPages().size());
    }

    private FetchResult successfulFetch(String url) {
        FetchResult fetchResult = mock(FetchResult.class);
        when(fetchResult.isSuccess()).thenReturn(true);

        return fetchResult;
    }

    private Webpage emptyPage(String url) {
        return new Webpage(new Link(url, false), new LinkedHashSet<>(), new ArrayList<>(), 1);
    }
}