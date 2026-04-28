package at.cc.main.javawebcrawler.core.extractor;

import at.cc.main.javawebcrawler.core.extractor.HtmlExtractor;
import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.HeadlineItem;
import at.cc.main.javawebcrawler.data.webpage.WebpageItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlExtractorTest {

    private Document doc;
    private FetchResult fetchResult;
    private HtmlExtractor htmlExtractor;
    private final int depth = 0;

    @BeforeEach
    void setup() {
        doc = Jsoup.parse("""
                    <html>
                        <body>
                            <h1>Main Headline</h1>
                            <h2>Subtitle 1</h2>
                            <h3>Subtitle 1.1</h3>
                            <h2>Subtitle 2</h2>
                
                            <a href="https://www.aau.at/home">AAU Home</a>
                            <a href="https://www.aau.at/studies">AAU Studies</a>
                        </body>
                    </html>
                """, "https://aau.at");

        fetchResult = new FetchResult("https://aau.at");

        htmlExtractor = new HtmlExtractor();
    }

    @Test
    void correctlyExtractsLinksAndHeadlines() {
        fetchResult.setSuccess(true);
        fetchResult.setDocument(doc);

        WebpageItem result = htmlExtractor.extractWebpage(fetchResult, depth);

        assertNotNull(result);

        assertEquals(2, result.getLinks().size());

        assertEquals(4, result.getHeadlines().size());
        assertEquals("Main Headline", result.getHeadlines().getFirst().getText());
        assertEquals("Subtitle 1", result.getHeadlines().get(1).getText());
        assertEquals(depth, result.getDepth());

        HeadlineItem mainHeadline = result.getHeadlines().getFirst();
        HeadlineItem subtitle1 = result.getHeadlines().get(1);
        HeadlineItem subtitle2 = result.getHeadlines().get(2);

        assertTrue(mainHeadline.getChildren().contains(subtitle1));
        assertEquals(mainHeadline, subtitle1.getParent());
        assertEquals(subtitle1, subtitle2.getParent());
    }

    @Test
    void correctlyHandleBrokenUrl() {
        fetchResult.setSuccess(false);
        fetchResult.setDocument(doc);

        WebpageItem result = htmlExtractor.extractWebpage(fetchResult, depth);

        assertNotNull(result);
        assertTrue(result.getRoot().isBroken());
        assertEquals(fetchResult.getUrl(), result.getRoot().link());
        assertNull(result.getLinks());
        assertNull(result.getHeadlines());
    }

    @Test
    void shouldReturnNullWhenDocumentIsNull() {
        fetchResult.setSuccess(true);
        fetchResult.setDocument(null);

        WebpageItem result = htmlExtractor.extractWebpage(fetchResult, depth);

        assertNull(result);
    }
}