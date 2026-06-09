package at.cc.main.javawebcrawler.core.engine;

import at.cc.main.javawebcrawler.core.extractor.HtmlExtractor;
import at.cc.main.javawebcrawler.core.fetcher.UrlFetcher;
import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.Link;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CrawlerEngineTest {

    private UrlFetcher urlFetcher;
    private HtmlExtractor htmlExtractor;
    private CrawlerEngine crawlerEngine;
    private List<String> domains;
    private ExecutorService pool;
    private final String url = "https://aau.at";

    @BeforeEach
    void setUp() {
        urlFetcher = mock(UrlFetcher.class);
        htmlExtractor = mock(HtmlExtractor.class);
        domains = new ArrayList<>();
        domains.add("aau.at");
        pool = Executors.newFixedThreadPool(10);
        crawlerEngine = new CrawlerEngine(2, domains, urlFetcher, htmlExtractor, pool);
    }

    @AfterEach
    void tearDown() {
        pool.shutdownNow();
    }

    @Test
    void crawlSinglePageSuccessfully() {
        FetchResult fetchResult = successfulFetch();
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        Webpage webpage = new Webpage(new Link(url, false), new LinkedHashSet<>(), new ArrayList<>(), 0);
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(webpage));

        crawlerEngine.crawl(url);

        assertEquals(1, crawlerEngine.getVisitedUrls().size());
        assertTrue(crawlerEngine.getVisitedUrls().contains(url));
    }

    @Test
    void crawlUpToMaxDepth() {
        String deepUrl = "https://aau.at/deep";
        FetchResult fetchResult = successfulFetch();
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link(deepUrl, false));

        Webpage webpage = new Webpage(new Link(url, false), links, new ArrayList<>(), 0);
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(webpage));

        crawlerEngine = createEngine(0, pool);
        crawlerEngine.crawl(url);

        assertEquals(1, crawlerEngine.getVisitedUrls().size());
    }

    @Test
    void crawlUrlsOnce() {
        FetchResult fetchResult = successfulFetch();

        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link(url, false));

        Webpage webpage = new Webpage(new Link(url, false), links, new ArrayList<>(), 0);
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(webpage));

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

        Webpage webpage = new Webpage(new Link(brokenUrl, true), null, null, 0);
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

        FetchResult fetchResult = successfulFetch();
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link(child1, false));
        links.add(new Link(child2, false));
        links.add(new Link(child3, false));
        Webpage rootPage = new Webpage(new Link(url, false), links, new ArrayList<>(), 0);
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(rootPage));

        for (String child : List.of(child1, child2, child3)) {
            FetchResult childFetchResult = successfulFetch();
            when(urlFetcher.fetchUrl(child)).thenReturn(childFetchResult);
            when(htmlExtractor.extractWebpage(childFetchResult, 1)).thenReturn(Optional.of(emptyPage(child)));
        }

        crawlerEngine = createEngine(1, pool);
        crawlerEngine.crawl(url);

        assertEquals(4, crawlerEngine.getVisitedUrls().size());
        assertTrue(crawlerEngine.getVisitedUrls().containsAll(List.of(url, child1, child2, child3)));
    }

    @Test
    void crawlsMultiplePagesAndCollectsAll() {
        String child1 = "https://aau.at/a";
        String child2 = "https://aau.at/b";

        FetchResult fetchResult = successfulFetch();
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);

        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link(child1, false));
        links.add(new Link(child2, false));
        Webpage rootPage = new Webpage(new Link(url, false), links, new ArrayList<>(), 0);
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(rootPage));

        for (String child : List.of(child1, child2)) {
            FetchResult childFetchResult = successfulFetch();
            when(urlFetcher.fetchUrl(child)).thenReturn(childFetchResult);
            when(htmlExtractor.extractWebpage(childFetchResult, 1)).thenReturn(Optional.of(emptyPage(child)));
        }

        crawlerEngine = createEngine(1, pool);
        crawlerEngine.crawl(url);

        assertEquals(3, crawlerEngine.getCrawledPages().size());
    }

    @Test
    void shutdownPoolCallsShutdown() throws InterruptedException {
        ExecutorService mockPool = mockPoolTerminatingInTime(true);

        createEngine(0, mockPool).shutdownPool();

        verify(mockPool).shutdown();
    }

    @Test
    void shutdownPoolDoesNotForceKillWhenFinishedInTime() throws InterruptedException {
        ExecutorService mockPool = mockPoolTerminatingInTime(true);

        createEngine(0, mockPool).shutdownPool();

        verify(mockPool, never()).shutdownNow();
    }

    @Test
    void shutdownPoolForceKillsWhenTimeoutExpires() throws InterruptedException {
        ExecutorService mockPool = mockPoolTerminatingInTime(false);

        createEngine(0, mockPool).shutdownPool();

        verify(mockPool).shutdownNow();
    }

    @Test
    void shutdownPoolForceKillsOnInterrupt() throws InterruptedException {
        ExecutorService mockPool = mock(ExecutorService.class);
        when(mockPool.awaitTermination(anyLong(), any(TimeUnit.class))).thenThrow(new InterruptedException());

        createEngine(0, mockPool).shutdownPool();

        verify(mockPool).shutdownNow();
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void crawlDoesNotCrashWhenFetcherThrowsRuntimeException() {
        when(urlFetcher.fetchUrl(url)).thenThrow(new RuntimeException("Unexpected internal error"));

        CrawlerEngine engine = createEngine(1, pool);

        assertDoesNotThrow(() -> engine.crawl(url));
    }

    @Test
    void crawlDoesNotCrashWhenExtractorThrowsRuntimeException() {
        FetchResult fetchResult = mock(FetchResult.class);
        when(fetchResult.isSuccess()).thenReturn(true);
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);
        when(htmlExtractor.extractWebpage(any(), anyInt())).thenThrow(new RuntimeException("Unexpected internal error"));

        CrawlerEngine engine = createEngine(1, pool);

        assertDoesNotThrow(() -> engine.crawl(url));
    }


    @Test
    void crawlContinuesWithRemainingUrlsWhenOneFetcherThrows() {
        String workingUrl = "https://aau.at/a";
        String brokenUrl = "https://aau.at/b";

        FetchResult fetchResult = successfulFetch();
        when(urlFetcher.fetchUrl(url)).thenReturn(fetchResult);
        when(urlFetcher.fetchUrl(workingUrl)).thenReturn(fetchResult);

        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link(workingUrl, false));
        links.add(new Link(brokenUrl, false));

        Webpage rootPage = new Webpage(new Link(url, false), links, new ArrayList<>(), 0);
        when(htmlExtractor.extractWebpage(fetchResult, 0)).thenReturn(Optional.of(rootPage));

        Webpage workingPage = new Webpage(new Link(workingUrl, false), new LinkedHashSet<>(), new ArrayList<>(), 1);
        when(htmlExtractor.extractWebpage(fetchResult, 1)).thenReturn(Optional.of(workingPage));

        when(urlFetcher.fetchUrl(brokenUrl)).thenThrow(new RuntimeException("Unexpected internal error"));

        CrawlerEngine engine = createEngine(1, pool);

        assertDoesNotThrow(() -> engine.crawl(url));
        assertTrue(engine.getVisitedUrls().contains(workingUrl));
    }

    private FetchResult successfulFetch() {
        FetchResult fetchResult = mock(FetchResult.class);
        when(fetchResult.isSuccess()).thenReturn(true);

        return fetchResult;
    }

    private Webpage emptyPage(String url) {
        return new Webpage(new Link(url, false), new LinkedHashSet<>(), new ArrayList<>(), 1);
    }

    private ExecutorService mockPoolTerminatingInTime(boolean terminatesInTime) throws InterruptedException {
        ExecutorService mockPool = mock(ExecutorService.class);
        when(mockPool.awaitTermination(anyLong(), any())).thenReturn(terminatesInTime);
        return mockPool;
    }

    private CrawlerEngine createEngine(int depth, ExecutorService pool) {
        return new CrawlerEngine(depth, domains, urlFetcher, htmlExtractor, pool);
    }
}