package at.cc.main.javawebcrawler.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DomainValidatorTest {

    @Test
    public void extractDomainReturnsCorrect() {
        Optional<String> domain = DomainValidator.extractDomain("https://aau.at");

        assertTrue(domain.isPresent());
        assertEquals("aau.at", domain.get());
    }

    @Test
    public void extractDomainHandlesInvalidInput() {
        Optional<String> domain = DomainValidator.extractDomain("test");

        assertTrue(domain.isEmpty());
    }

    @Test
    void allowExactDomainMatch() {
        String url = "https://aau.at";
        List<String> allowed = List.of("aau.at");

        assertTrue(DomainValidator.isAllowedDomain(url, allowed));
    }

    @Test
    void allowWwwVariation() {
        String url = "https://www.aau.at";
        List<String> allowed = List.of("aau.at");

        assertTrue(DomainValidator.isAllowedDomain(url, allowed));
    }

    @Test
    void allowSubdomain() {
        String url = "https://test.aau.at";
        List<String> allowed = List.of("aau.at");

        assertTrue(DomainValidator.isAllowedDomain(url, allowed));
    }

    @Test
    void allowNestedSubdomain() {
        String url = "https://another.test.aau.at";
        List<String> allowed = List.of("aau.at");

        assertTrue(DomainValidator.isAllowedDomain(url, allowed));
    }

    @Test
    void rejectSimilarButInvalidDomain() {
        String url = "https://notaau.at";
        List<String> allowed = List.of("aau.at");

        assertFalse(DomainValidator.isAllowedDomain(url, allowed));
    }

    @Test
    void handleAllowedDomainWithWww() {
        String url = "https://aau.at";
        List<String> allowed = List.of("www.aau.at");

        assertTrue(DomainValidator.isAllowedDomain(url, allowed));
    }

    @Test
    void returnFalseForInvalidUrl() {
        String url = "invalid-url";
        List<String> allowed = List.of("aau.at");

        assertFalse(DomainValidator.isAllowedDomain(url, allowed));
    }

    @Test
    void returnFalseWhenHostIsNull() {
        String url = "http:///invalid";
        List<String> allowed = List.of("aau.at");

        assertFalse(DomainValidator.isAllowedDomain(url, allowed));
    }

    @Test
    void workWithMultipleAllowedDomains() {
        String url = "https://test.com";
        List<String> allowed = List.of("aau.at", "test.com");

        assertTrue(DomainValidator.isAllowedDomain(url, allowed));
    }
}
