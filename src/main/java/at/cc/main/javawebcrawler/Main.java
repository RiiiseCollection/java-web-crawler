package at.cc.main.javawebcrawler;

import at.cc.main.javawebcrawler.engine.CrawlerEngine;
import at.cc.main.javawebcrawler.exception.InputValidationException;
import at.cc.main.javawebcrawler.report.MarkdownReportGenerator;
import at.cc.main.javawebcrawler.util.InputValidator;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            InputValidator.validateInput(args);

            String startUrl = args[0];
            int maxDepth = Integer.parseInt(args[1]);
            List<String> allowedDomains = new ArrayList<>();

            for (int i = 2; i < args.length; i++) {
                allowedDomains.add(args[i]);
            }

            System.out.println("Start URL: " + startUrl);
            System.out.println("Max depth: " + maxDepth);
            System.out.println("Allowed domains: " + allowedDomains);

            CrawlerEngine crawler = new CrawlerEngine(maxDepth, allowedDomains);
            crawler.crawl(startUrl);

            MarkdownReportGenerator reportGenerator = new MarkdownReportGenerator();
            reportGenerator.generateReport(crawler.getCrawledPages());

            System.out.println("\nCrawling completed successfully!");
            System.out.println("Pages crawled: " + crawler.getVisitedUrls().size());

        } catch (InputValidationException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}