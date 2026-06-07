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

    private static final Path REPORT_PATH = Path.of("crawl-report.md");
    private MarkdownReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        reportGenerator = new MarkdownReportGenerator();
    }

    @Test
    void generateBasicReportFormat() throws IOException {
        List<Webpage> pages = new ArrayList<>();
        pages.add(new Webpage(new Link("www.sample-input.com", false), new LinkedHashSet<>(), new ArrayList<>(), 1));

        assertReportContains(pages, "input: <a>www.sample-input.com</a>", "<br>depth: 1");
    }

    @Test
    void highlightBrokenLinks() throws IOException {
        List<Webpage> pages = new ArrayList<>();
        pages.add(new Webpage(new Link("https://sample-input.com/broken", true), null, null, 0));

        assertReportContains(pages, "broken link <a>https://sample-input.com/broken</a>");
    }

    @Test
    void handleEmptyList() throws IOException {
        reportGenerator.generateReport(new ArrayList<>());

        assertTrue(Files.exists(REPORT_PATH));
        Files.deleteIfExists(REPORT_PATH);
    }

    @Test
    void writesHeadlinesWithCorrectMarkdown() throws IOException {
        Headline h1 = new Headline(HeaderLevel.H1, "Main Title", null);
        Headline h2 = new Headline(HeaderLevel.H2, "Subtitle", h1);
        h1.addChild(h2);

        List<Webpage> pages = new ArrayList<>();
        pages.add(new Webpage(new Link("https://sample-input.com", false), new LinkedHashSet<>(), List.of(h1, h2), 0));

        assertReportContains(pages, "# -> Main Title", "## -> Subtitle");
    }

    @Test
    void writesSinglePageLinkTree() throws IOException {
        LinkedHashSet<Link> links = new LinkedHashSet<>();
        links.add(new Link("https://sample-input.com/page", false));
        links.add(new Link("https://sample-input.com/broken", true));

        List<Webpage> pages = new ArrayList<>();
        pages.add(new Webpage(new Link("https://sample-input.com", false), links, new ArrayList<>(), 0));

        assertReportContains(pages, "link to <a>https://sample-input.com/page</a>", "broken link <a>https://sample-input.com/broken</a>");
    }

    private String generateAndReadReport(List<Webpage> pages) throws IOException {
        reportGenerator.generateReport(pages);
        String content = Files.readString(REPORT_PATH);
        Files.deleteIfExists(REPORT_PATH);
        return content;
    }

    private void assertReportContains(List<Webpage> pages, String... expectedStrings) throws IOException {
        String content = generateAndReadReport(pages);
        for (String fragment : expectedStrings) {
            assertTrue(content.contains(fragment));
        }
    }
}