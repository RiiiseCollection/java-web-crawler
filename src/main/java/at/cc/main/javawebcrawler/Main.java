package at.cc.main.javawebcrawler;

import at.cc.main.javawebcrawler.core.engine.CrawlerEngine;
import at.cc.main.javawebcrawler.exception.InputValidationException;
import at.cc.main.javawebcrawler.report.MarkdownReportGenerator;
import at.cc.main.javawebcrawler.validator.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    static void main(String[] args) {

        try {
            InputValidator.validateInput(args);

            String startUrl = args[0];
            int maxDepth = Integer.parseInt(args[1]);
            List<String> allowedDomains = new ArrayList<>();

            for (int i = 2; i < args.length; i++) {
                allowedDomains.add(args[i]);
            }

            log.info("Start URL: {}", startUrl);
            log.info("Max depth: {}", maxDepth);
            log.info("Allowed domains: {}", allowedDomains);

            CrawlerEngine crawler = new CrawlerEngine(maxDepth, allowedDomains);
            crawler.crawl(startUrl);

            MarkdownReportGenerator reportGenerator = new MarkdownReportGenerator();
            reportGenerator.generateReport(crawler.getCrawledPages());

            log.info("\nCrawling completed successfully!\nPages crawled: {}", crawler.getVisitedUrls().size());

        } catch (InputValidationException e) {
            log.error("Invalid input: {}", e.getMessage());
            System.exit(2);
        }
    }
}