package at.cc.main.javawebcrawler.core.engine;

import at.cc.main.javawebcrawler.core.extractor.HtmlExtractor;
import at.cc.main.javawebcrawler.core.fetcher.UrlFetcher;
import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.Link;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import at.cc.main.javawebcrawler.network.JsoupHttpClient;
import at.cc.main.javawebcrawler.util.DomainValidator;
import at.cc.main.javawebcrawler.util.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class CrawlerEngine {
    private static final Logger log = LoggerFactory.getLogger(CrawlerEngine.class);

    private static final int THREAD_POOL_SIZE = 10;
    private static final long TIMEOUT_SECONDS = 300;

    private final UrlFetcher urlFetcher;
    private final HtmlExtractor htmlExtractor;
    private final Set<String> visitedUrls;
    private final List<Webpage> crawledPages;
    private final List<String> allowedDomains;
    private final int maxDepth;

    private final ExecutorService pool;

    public CrawlerEngine(int maxDepth, List<String> allowedDomains) {
        this(maxDepth, allowedDomains, new UrlFetcher(new JsoupHttpClient()), new HtmlExtractor(), Executors.newFixedThreadPool(THREAD_POOL_SIZE));
    }

    public CrawlerEngine(int maxDepth, List<String> allowedDomains, UrlFetcher urlFetcher, HtmlExtractor htmlExtractor, ExecutorService pool) {
        this.urlFetcher = urlFetcher;
        this.htmlExtractor = htmlExtractor;
        this.visitedUrls = ConcurrentHashMap.newKeySet();
        this.crawledPages = new CopyOnWriteArrayList<>();
        this.allowedDomains = allowedDomains;
        this.maxDepth = maxDepth;
        this.pool = pool;
    }

    public void crawl(String startUrl) {
        try {
            crawlLevel(List.of(startUrl), 0, pool);
        } finally {
            shutdownPool();
        }
    }

    private void crawlLevel(List<String> urls, int currentDepth, ExecutorService pool) {
        if (currentDepth > maxDepth || urls.isEmpty()) {
            return;
        }

        List<Future<List<String>>> futures = new ArrayList<>();
        for (String url : urls) {
            futures.add(pool.submit(new CrawlTask(url, currentDepth)));
        }

        List<String> nextLevelUrls = new ArrayList<>();
        for (Future<List<String>> future : futures) {
            try {
                nextLevelUrls.addAll(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Crawl interrupted at depth {}", currentDepth);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                String msg = (cause != null) ? cause.getMessage() : e.getMessage();
                log.error("Error during crawling at depth {}: {}", currentDepth, msg);
            }
        }

        crawlLevel(nextLevelUrls, currentDepth + 1, pool);
    }

    void shutdownPool() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warn("Thread pool did not terminate in time, forcing shutdown.");
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for thread pool to terminate, forcing shutdown.");
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public List<Webpage> getCrawledPages() {
        return crawledPages;
    }

    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }

    private class CrawlTask implements Callable<List<String>> {

        private final String url;
        private final int currentDepth;

        CrawlTask(String url, int currentDepth) {
            this.url = url;
            this.currentDepth = currentDepth;
        }

        @Override
        public List<String> call() {
            if (!isEligible(url)) {
                return List.of();
            }

            log.info("Crawling: {} at depth {}", url, currentDepth);

            try {
                FetchResult fetchResult = urlFetcher.fetchUrl(url);
                return processAndCollectChildren(fetchResult);
            } catch (Exception e) {
                log.error("Unexpected error crawling {}: {}", url, e.getMessage(), e);
                return List.of();
            }
        }

        private boolean isEligible(String url) {
            if (currentDepth > maxDepth) return false;
            if (!DomainValidator.isAllowedDomain(url, allowedDomains)) return false;
            if (!UrlValidator.isHtmlUrl(url)) return false;

            return visitedUrls.add(url);
        }

        private List<String> processAndCollectChildren(FetchResult fetchResult) {
            return htmlExtractor.extractWebpage(fetchResult, currentDepth)
                    .map(page -> {
                        crawledPages.add(page);
                        return collectChildUrls(fetchResult, page);
                    })
                    .orElse(List.of());
        }

        private List<String> collectChildUrls(FetchResult fetchResult, Webpage page) {
            if (!fetchResult.isSuccess() || currentDepth >= maxDepth) {
                return List.of();
            }
            return page.links().stream()
                    .filter(link -> !link.isBroken())
                    .map(Link::link)
                    .toList();
        }
    }
}