package at.cc.main.javawebcrawler.report;

import at.cc.main.javawebcrawler.data.WebpageItem;
import at.cc.main.javawebcrawler.data.HeadlineItem;
import at.cc.main.javawebcrawler.data.LinkItem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MarkdownReportGenerator {
    private static final String REPORT_FILENAME = "crawl-report.md";

    public void generateReport(List<WebpageItem> crawledPages) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FILENAME))) {
            for (WebpageItem page : crawledPages) {
                writePageEntry(writer, page);
            }
            System.out.println("Report generated: " + REPORT_FILENAME);
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    private void writePageEntry(BufferedWriter writer, WebpageItem page) throws IOException {
        LinkItem root = page.getRoot();

        writer.write("input: <a>" + root.link() + "</a>\n");
        writer.write("<br>depth: " + page.getDepth() + "\n");

        if (root.isBroken()) {
            writer.write("\n<br>--> broken link <a>" + root.link() + "</a>\n\n");
            return;
        }

        List<HeadlineItem> headlines = page.getHeadlines();
        if (headlines != null) {
            for (HeadlineItem headline : headlines) {
                if (headline.isRoot()) {
                    writeHeadlineTree(writer, headline, page.getDepth());
                }
            }
        }

        writer.write("\n");
    }

    private void writeHeadlineTree(BufferedWriter writer, HeadlineItem headline, int depth) throws IOException {
        String headingMarkers = "#".repeat(headline.getHeaderLevel().getLevel());
        writer.write(headingMarkers + " " + getArrowPrefix(depth) + headline.getText() + "\n");

        for (HeadlineItem child : headline.getChildren()) {
            writeHeadlineTree(writer, child, depth);
        }
    }

    private String getArrowPrefix(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("-");
        }
        sb.append("> ");
        return sb.toString();
    }
}