package at.cc.main.javawebcrawler.core.engine;

import at.cc.main.javawebcrawler.core.extractor.HtmlExtractor;
import at.cc.main.javawebcrawler.core.fetcher.UrlFetcher;
import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.Link;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import at.cc.main.javawebcrawler.network.JsoupHttpClient;
import at.cc.main.javawebcrawler.util.DomainValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class CrawlerEngine {
    private static final int THREAD_POOL_SIZE = 10;
    private static final long TIMEOUT_SECONDS = 300;

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
        this.visitedUrls = ConcurrentHashMap.newKeySet();
        this.crawledPages = new CopyOnWriteArrayList<>();
        this.allowedDomains = allowedDomains;
        this.maxDepth = maxDepth;
    }

    public void crawl(String startUrl) {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try {
            crawlLevel(List.of(startUrl), 0, pool);
        } finally {
            shutdownPool(pool);
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
                System.err.println("Crawl interrupted at depth " + currentDepth);
            } catch (ExecutionException e) {
                System.err.println("Error during crawling: " + e.getCause().getMessage());
            }
        }

        crawlLevel(nextLevelUrls, currentDepth + 1, pool);
    }

    private void shutdownPool(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
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

            System.out.println("Crawling: " + url + " at depth " + currentDepth);

            FetchResult fetchResult = urlFetcher.fetchUrl(url);
            return processAndCollectChildren(fetchResult);
        }

        private boolean isEligible(String url) {
            if (currentDepth > maxDepth) return false;
            if (!DomainValidator.isAllowedDomain(url, allowedDomains)) return false;

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