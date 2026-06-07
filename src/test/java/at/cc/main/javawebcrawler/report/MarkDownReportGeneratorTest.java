package at.cc.main.javawebcrawler.report;

import at.cc.main.javawebcrawler.data.webpage.HeaderLevel;
import at.cc.main.javawebcrawler.data.webpage.Headline;
import at.cc.main.javawebcrawler.data.webpage.Link;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownReportGeneratorTest {

    private MarkdownReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        reportGenerator = new MarkdownReportGenerator();
    }

    @Test
    void generateBasicReportFormat() throws IOException {
        List<Webpage> pages = new ArrayList<>();
        Link root = new Link("www.sample-input.com", false);
        pages.add(new Webpage(root, new LinkedHashSet<>(), new ArrayList<>(), 1));

        reportGenerator.generateReport(pages);

        String content = Files.readString(Path.of("crawl-report.md"));
        assertTrue(content.contains("input: <a>www.sample-input.com</a>"));
        assertTrue(content.contains("<br>depth: 1"));

        Files.deleteIfExists(Path.of("crawl-report.md"));
    }

    @Test
    void highlightBrokenLinks() throws IOException {
        List<Webpage> pages = new ArrayList<>();
        Link root = new Link("www.broken-404.com", true);
        pages.add(new Webpage(root, null, null, 0));

        reportGenerator.generateReport(pages);

        String content = Files.readString(Path.of("crawl-report.md"));
        assertTrue(content.contains("broken link <a>www.broken-404.com</a>"));

        Files.deleteIfExists(Path.of("crawl-report.md"));
    }

    @Test
    void handleEmptyList() throws IOException {
        List<Webpage> pages = new ArrayList<>();

        reportGenerator.generateReport(pages);

        assertTrue(Files.exists(Path.of("crawl-report.md")));

        Files.deleteIfExists(Path.of("crawl-report.md"));
    }

    @Test
    void writesHeadlinesWithCorrectMarkdown() throws IOException {
        Headline h1 = new Headline(HeaderLevel.H1, "Main Title", null);
        Headline h2 = new Headline(HeaderLevel.H2, "Subtitle", h1);
        h1.addChild(h2);

        List<Webpage> pages = new ArrayList<>();
        pages.add(new Webpage(new Link("https://sample-input.com", false), new LinkedHashSet<>(), List.of(h1, h2), 0));

        reportGenerator.generateReport(pages);

        String content = Files.readString(Path.of("crawl-report.md"));
        assertTrue(content.contains("# -> Main Title"));
        assertTrue(content.contains("## -> Subtitle"));

        Files.deleteIfExists(Path.of("crawl-report.md"));
    }

    @Test
    void writesSinglePageLinkTree() throws IOException {
        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link("https://sample-input.com/page", false));
        links.add(new Link("https://sample-input.com/broken", true));

        List<Webpage> pages = new ArrayList<>();
        pages.add(new Webpage(new Link("https://sample-input.com", false), links, new ArrayList<>(), 0));

        reportGenerator.generateReport(pages);

        String content = Files.readString(Path.of("crawl-report.md"));
        assertTrue(content.contains("link to <a>https://sample-input.com/page</a>"));
        assertTrue(content.contains("broken link <a>https://sample-input.com/broken</a>"));

        Files.deleteIfExists(Path.of("crawl-report.md"));
    }
}