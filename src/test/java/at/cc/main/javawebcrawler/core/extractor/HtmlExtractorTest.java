package at.cc.main.javawebcrawler.core.extractor;

import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.webpage.Headline;
import at.cc.main.javawebcrawler.data.webpage.Webpage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlExtractorTest {

    private String body;
    private String url;
    private FetchResult fetchResult;
    private HtmlExtractor htmlExtractor;
    private final int depth = 0;

    @BeforeEach
    void setup() {
        body = """
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
                """;
        url = "https://aau.at";

        fetchResult = new FetchResult(url);

        htmlExtractor = new HtmlExtractor();
    }

    @Test
    void correctlyExtractsLinksAndHeadlines() {
        fetchResult.setSuccess(true);
        fetchResult.setBody(body);

        Optional<Webpage> result = htmlExtractor.extractWebpage(fetchResult, depth);

        assertTrue(result.isPresent());
        Webpage webpage = result.get();

        assertEquals(2, webpage.links().size());

        assertEquals(4, webpage.headlines().size());
        assertEquals("Main Headline", webpage.headlines().getFirst().getText());
        assertEquals("Subtitle 1", webpage.headlines().get(1).getText());
        assertEquals(depth, webpage.depth());

        Headline mainHeadline = webpage.headlines().getFirst();
        Headline subtitle1 = webpage.headlines().get(1);
        Headline subtitle2 = webpage.headlines().get(2);

        assertTrue(mainHeadline.getChildren().contains(subtitle1));
        assertEquals(mainHeadline, subtitle1.getParent());
        assertEquals(subtitle1, subtitle2.getParent());
    }

    @Test
    void correctlyHandlesBrokenUrl() {
        fetchResult.setSuccess(false);
        fetchResult.setBody(body);

        Optional<Webpage> result = htmlExtractor.extractWebpage(fetchResult, depth);

        assertTrue(result.isPresent());
        Webpage webpage = result.get();

        assertTrue(webpage.root().isBroken());
        assertEquals(fetchResult.getUrl(), webpage.root().link());
        assertTrue(webpage.links().isEmpty());
        assertTrue(webpage.headlines().isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenBodyIsNull() {
        fetchResult.setSuccess(true);
        fetchResult.setBody(null);

        Optional<Webpage> result = htmlExtractor.extractWebpage(fetchResult, depth);

        assertTrue(result.isEmpty());
    }
}