package at.cc.main.javawebcrawler.report;

import at.cc.main.javawebcrawler.data.webpage.Headline;
import at.cc.main.javawebcrawler.data.webpage.Link;
import at.cc.main.javawebcrawler.data.webpage.Webpage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MarkdownReportGenerator {
    private static final String REPORT_FILENAME = "crawl-report.md";

    public void generateReport(List<Webpage> crawledPages) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FILENAME))) {
            for (Webpage page : crawledPages) {
                writePageEntry(writer, page);

                if (crawledPages.size() == 1) {
                    writeLinkTree(writer, page);
                }
            }
            System.out.println("Report generated: " + REPORT_FILENAME);
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    private void writePageEntry(BufferedWriter writer, Webpage page) throws IOException {
        Link root = page.root();

        writer.write("input: <a>" + root.link() + "</a>\n");
        writer.write("<br>depth: " + page.depth() + "\n");

        if (root.isBroken()) {
            writer.write("\n<br>--> broken link <a>" + root.link() + "</a>\n\n");
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