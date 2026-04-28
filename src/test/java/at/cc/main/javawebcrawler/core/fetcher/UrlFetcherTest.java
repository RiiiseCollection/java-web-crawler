package at.cc.main.javawebcrawler.core.fetcher;

import at.cc.main.javawebcrawler.core.fetcher.UrlFetcher;
import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.network.HttpClient;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlFetcherTest {
    private HttpClient httpClient;
    private Connection.Response response;
    private Document document;
    private UrlFetcher urlFetcher;
    private String url;

    @BeforeEach
    void setup() {
        httpClient = mock(HttpClient.class);
        response = mock(Connection.Response.class);
        document = mock(Document.class);

        urlFetcher = new UrlFetcher(httpClient);

        url = "https://aau.at";
    }

    @Test
    void noSuccessUrlWasNull() throws IOException {
        when(httpClient.getUrl(url)).thenReturn(null);

        FetchResult fetchResult = urlFetcher.fetchUrl(url);

        assertFalse(fetchResult.isSuccess());
        assertEquals("Provided url was null", fetchResult.getErrorMsg());
        assertEquals(url, fetchResult.getUrl());
        assertTrue(fetchResult.isBrokenUrl());
    }

    @Test
    void successfulFetchResultOnStatus200() throws IOException {
        int statusCode = 200;

        when(httpClient.getUrl(url)).thenReturn(response);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.parse()).thenReturn(document);

        FetchResult fetchResult = urlFetcher.fetchUrl(url);

        assertTrue(fetchResult.isSuccess());
        assertEquals(statusCode, fetchResult.getStatusCode());
        assertEquals(document, fetchResult.getDocument());
        assertEquals(url, fetchResult.getUrl());
        assertNull(fetchResult.getErrorMsg());
        assertFalse(fetchResult.isBrokenUrl());
    }

    @Test
    void unsuccessfulFetchResultOnStatusNot200() throws IOException {
        int statusCode = 404;
        when(httpClient.getUrl(url)).thenReturn(response);
        when(response.statusCode()).thenReturn(statusCode);

        FetchResult fetchResult = urlFetcher.fetchUrl(url);

        assertFalse(fetchResult.isSuccess());
        assertEquals(statusCode, fetchResult.getStatusCode());
        assertNull(fetchResult.getDocument());
        assertEquals(url, fetchResult.getUrl());
        assertEquals("Error fetching Url. StatusCode: " + statusCode, fetchResult.getErrorMsg());
        assertTrue(fetchResult.isBrokenUrl());
    }

    @Test
    void throwsIOExceptionOnGetUrlError() throws IOException {
        String errorMessage = "Could not connect to url";
        when(httpClient.getUrl(url)).thenThrow(new IOException(errorMessage));

        FetchResult fetchResult = urlFetcher.fetchUrl(url);

        assertFalse(fetchResult.isSuccess());
        assertNull(fetchResult.getDocument());
        assertEquals(url, fetchResult.getUrl());
        assertEquals(errorMessage, fetchResult.getErrorMsg());
        assertTrue(fetchResult.isBrokenUrl());
    }

    @Test
    void throwsIOExceptionOnParseError() throws IOException {
        String errorMessage = "Failed to parse document";
        int statusCode = 200;
        when(httpClient.getUrl(url)).thenReturn(response);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.parse()).thenThrow(new IOException(errorMessage));

        FetchResult fetchResult = urlFetcher.fetchUrl(url);

        assertFalse(fetchResult.isSuccess());
        assertNull(fetchResult.getDocument());
        assertEquals(url, fetchResult.getUrl());
        assertEquals(errorMessage, fetchResult.getErrorMsg());
        assertTrue(fetchResult.isBrokenUrl());
    }

}
