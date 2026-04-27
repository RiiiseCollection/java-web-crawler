package at.cc.main.javawebcrawler.util;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

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
}
