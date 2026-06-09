package at.cc.main.javawebcrawler.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlValidatorTest {

    @Test
    void shouldReturnTrueForValidHtml() {
        assertTrue(UrlValidator.isHtmlUrl("https://test.com/test.html"));
    }

    @Test
    void shouldReturnTrueForValidHtm() {
        assertTrue(UrlValidator.isHtmlUrl("https://test.com/test.htm"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://test.com/",
            "https://test.com/test",
            "https://test.com/test?test=test",
            ""
    })
    void shouldReturnTrueForUnknownContentType(String url) {
        assertTrue(UrlValidator.isHtmlUrl(url));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://test.com/test.png",
            "https://test.com/test.jpg",
            "https://test.com/test.gif",
            "https://test.com/test.webp",
            "https://test.com/test.zip",
            "https://test.com/test.mp3"
    })
    void shouldReturnFalseForNonHtmlUrls(String url) {
        assertFalse(UrlValidator.isHtmlUrl(url));
    }

    @Test
    void shouldReturnFalseForNullUrl() {
        assertFalse(UrlValidator.isHtmlUrl(null));
    }
}
