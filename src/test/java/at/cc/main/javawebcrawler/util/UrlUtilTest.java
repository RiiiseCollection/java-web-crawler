package at.cc.main.javawebcrawler.util;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UrlUtilTest {

    @Test
    public void getDomainReturnsCorrect() throws URISyntaxException {
        UrlUtil urlUtil = new UrlUtil();

        assertEquals("aau.at", urlUtil.getDomain("https://aau.at"));
    }

    @Test
    public void getDomainHandelsInvalidInput() throws URISyntaxException {
        UrlUtil urlUtil = new UrlUtil();

        assertNull(urlUtil.getDomain("test"));
    }

    @Test
    void allowExactDomainMatch() {
        String url = "https://aau.at";
        List<String> allowed = List.of("aau.at");

        assertTrue(UrlUtil.isAllowedDomain(url, allowed));
    }

    @Test
    void allowWwwVariation() {
        String url = "https://www.aau.at";
        List<String> allowed = List.of("aau.at");

        assertTrue(UrlUtil.isAllowedDomain(url, allowed));
    }

    @Test
    void allowSubdomain() {
        String url = "https://test.aau.at";
        List<String> allowed = List.of("aau.at");

        assertTrue(UrlUtil.isAllowedDomain(url, allowed));
    }

    @Test
    void allowNestedSubdomain() {
        String url = "https://another.test.aau.at";
        List<String> allowed = List.of("aau.at");

        assertTrue(UrlUtil.isAllowedDomain(url, allowed));
    }

    @Test
    void rejectSimilarButInvalidDomain() {
        String url = "https://notaau.at";
        List<String> allowed = List.of("aau.at");

        assertFalse(UrlUtil.isAllowedDomain(url, allowed));
    }

    @Test
    void handleAllowedDomainWithWww() {
        String url = "https://aau.at";
        List<String> allowed = List.of("www.aau.at");

        assertTrue(UrlUtil.isAllowedDomain(url, allowed));
    }

    @Test
    void returnFalseForInvalidUrl() {
        String url = "invalid-url";
        List<String> allowed = List.of("aau.at");

        assertFalse(UrlUtil.isAllowedDomain(url, allowed));
    }

    @Test
    void returnFalseWhenHostIsNull() {
        String url = "http:///invalid";
        List<String> allowed = List.of("aau.at");

        assertFalse(UrlUtil.isAllowedDomain(url, allowed));
    }

    @Test
    void workWithMultipleAllowedDomains() {
        String url = "https://test.com";
        List<String> allowed = List.of("aau.at", "test.com");

        assertTrue(UrlUtil.isAllowedDomain(url, allowed));
    }
}
