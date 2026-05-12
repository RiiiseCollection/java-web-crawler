package at.cc.main.javawebcrawler.core.extractor;

import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.Headline;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

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

        Webpage result = htmlExtractor.extractWebpage(fetchResult, depth);

        assertNotNull(result);

        assertEquals(2, result.links().size());

        assertEquals(4, result.headlines().size());
        assertEquals("Main Headline", result.headlines().getFirst().getText());
        assertEquals("Subtitle 1", result.headlines().get(1).getText());
        assertEquals(depth, result.depth());

        Headline mainHeadline = result.headlines().getFirst();
        Headline subtitle1 = result.headlines().get(1);
        Headline subtitle2 = result.headlines().get(2);

        assertTrue(mainHeadline.getChildren().contains(subtitle1));
        assertEquals(mainHeadline, subtitle1.getParent());
        assertEquals(subtitle1, subtitle2.getParent());
    }

    @Test
    void correctlyHandleBrokenUrl() {
        fetchResult.setSuccess(false);
        fetchResult.setDocument(doc);

        Webpage result = htmlExtractor.extractWebpage(fetchResult, depth);

        assertNotNull(result);
        assertTrue(result.root().isBroken());
        assertEquals(fetchResult.getUrl(), result.root().link());
        assertNull(result.links());
        assertNull(result.headlines());

    }

    @Test
    void shouldReturnNullWhenDocumentIsNull() {
        fetchResult.setSuccess(true);
        fetchResult.setDocument(null);

        Webpage result = htmlExtractor.extractWebpage(fetchResult, depth);

        assertNull(result);
    }
}