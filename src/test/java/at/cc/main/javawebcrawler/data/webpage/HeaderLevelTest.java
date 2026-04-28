package at.cc.main.javawebcrawler.data.webpage;

import org.jsoup.parser.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HeaderLevelTest {

    @Test
    void tagToLevelReturnsNullWhenTagIsNull() {
        Tag tag = null;

        HeaderLevel level = HeaderLevel.tagToLevel(tag);

        assertNull(level);
    }

    @ParameterizedTest
    @ValueSource(strings = {"H1", "H2", "H3", "H4", "H5", "H6", "h1", "h2", "h3", "h4", "h5", "h6"})
    void correctlyConvertsToHeader(String tagName) {
        Tag tag = new Tag(tagName);

        HeaderLevel expected = HeaderLevel.valueOf(tagName.toUpperCase());

        assertEquals(expected, HeaderLevel.tagToLevel(tag));
    }

}

