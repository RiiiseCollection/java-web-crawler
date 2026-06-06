package at.cc.main.javawebcrawler.data.webpage;

import org.jsoup.parser.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HeaderLevelTest {

    @Test
    void tagToLevelThrowsWhenTagIsNull() {
        assertThrows(IllegalArgumentException.class, () -> HeaderLevel.tagToLevel(null));
    }

    @Test
    void tagToLevelReturnsEmptyForUnknownTag() {
        Tag tag = Tag.valueOf("div");

        Optional<HeaderLevel> result = HeaderLevel.tagToLevel(tag);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"H1", "H2", "H3", "H4", "H5", "H6", "h1", "h2", "h3", "h4", "h5", "h6"})
    void correctlyConvertsToHeader(String tagName) {
        Tag tag = Tag.valueOf(tagName);

        Optional<HeaderLevel> result = HeaderLevel.tagToLevel(tag);

        assertTrue(result.isPresent());
        assertEquals(HeaderLevel.valueOf(tagName.toUpperCase()), result.get());
    }

}

