package at.cc.main.javawebcrawler.report;

import at.cc.main.javawebcrawler.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownReportGeneratorTest {

    private MarkdownReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        reportGenerator = new MarkdownReportGenerator();
    }

    @Test
    void generateBasicReportFormat() throws IOException {
        List<WebpageItem> pages = new ArrayList<>();
        LinkItem root = new LinkItem("www.sample-input.com", false);
        pages.add(new WebpageItem(root, new LinkedHashSet<>(), new ArrayList<>(), 1));

        reportGenerator.generateReport(pages);

        String content = Files.readString(Path.of("crawl-report.md"));
        assertTrue(content.contains("input: <a>www.sample-input.com</a>"));
        assertTrue(content.contains("<br>depth: 1"));

        Files.deleteIfExists(Path.of("crawl-report.md"));
    }

    @Test
    void highlightBrokenLinks() throws IOException {
        List<WebpageItem> pages = new ArrayList<>();
        LinkItem root = new LinkItem("www.broken-404.com", true);
        pages.add(new WebpageItem(root, null, null, 0));

        reportGenerator.generateReport(pages);

        String content = Files.readString(Path.of("crawl-report.md"));
        assertTrue(content.contains("broken link <a>www.broken-404.com</a>"));

        Files.deleteIfExists(Path.of("crawl-report.md"));
    }

    @Test
    void handleEmptyList() throws IOException {
        List<WebpageItem> pages = new ArrayList<>();

        reportGenerator.generateReport(pages);

        assertTrue(Files.exists(Path.of("crawl-report.md")));

        Files.deleteIfExists(Path.of("crawl-report.md"));
    }
}