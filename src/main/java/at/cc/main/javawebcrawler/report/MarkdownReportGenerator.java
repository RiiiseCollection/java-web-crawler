package at.cc.main.javawebcrawler.report;

import at.cc.main.javawebcrawler.data.webpage.Headline;
import at.cc.main.javawebcrawler.data.webpage.Link;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MarkdownReportGenerator {
    private static final Logger log = LoggerFactory.getLogger(MarkdownReportGenerator.class);

    private static final String REPORT_FILENAME = "crawl-report.md";

    public void generateReport(List<Webpage> crawledPages) {
        if (crawledPages.size() == 1) {
            generateSinglePageReport(crawledPages.getFirst());
        } else {
            generateMultiPageReport(crawledPages);
        }
    }

    public void generateSinglePageReport(Webpage page) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FILENAME))) {
            writePageEntry(writer, page);
            writeLinkTree(writer, page);
            log.info("Report generated: {}", REPORT_FILENAME);
        } catch (IOException e) {
            log.error("Error generating report: {}", e.getMessage(), e);
        }
    }

    public void generateMultiPageReport(List<Webpage> crawledPages) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FILENAME))) {
            for (Webpage page : crawledPages) {
                writePageEntry(writer, page);
            }
            log.info("Report generated: {}", REPORT_FILENAME);
        } catch (IOException e) {
            log.error("Error generating report: {}", e.getMessage(), e);
        }
    }

    private void writePageEntry(BufferedWriter writer, Webpage page) throws IOException {
        Link root = page.root();

        writer.write("input: <a>" + root.link() + "</a>\n");
        writer.write("<br>depth: " + page.depth() + "\n");

        if (root.isBroken()) {
            writer.write("\n<br>--> broken link <a>" + root.link() + "</a>\n\n");
            log.warn("Broken link encountered: {}", root.link());
            return;
        }

        List<Headline> headlines = page.headlines();
        if (headlines != null) {
            for (Headline headline : headlines) {
                if (headline.isRoot()) {
                    writeHeadlineTree(writer, headline, page.depth());
                }
            }
        }

        writer.write("\n");
    }

    private void writeHeadlineTree(BufferedWriter writer, Headline headline, int depth) throws IOException {
        String headingMarkers = "#".repeat(headline.getHeaderLevel().getLevel());
        writer.write(headingMarkers + " " + getArrowPrefix(depth) + headline.getText() + "\n");

        for (Headline child : headline.getChildren()) {
            writeHeadlineTree(writer, child, depth);
        }
    }

    private void writeLinkTree(BufferedWriter writer, Webpage page) throws IOException {
        Set<Link> links = page.links();
        if (links != null && !links.isEmpty()) {
            for (Link link : links) {
                String arrowPrefix = getArrowPrefix(page.depth());
                if (link.isBroken()) {
                    writer.write("<br> " + arrowPrefix + "broken link <a>" + link.link() + "</a>\n");
                    log.warn("Broken link in page {}: {}", page.root().link(), link.link());
                } else {
                    writer.write("<br> " + arrowPrefix + "link to <a>" + link.link() + "</a>\n");
                }
            }
        }
    }

    private String getArrowPrefix(int depth) {
        return "-".repeat(Math.max(0, depth + 1)) + "> ";
    }
}