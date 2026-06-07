package at.cc.main.javawebcrawler.core.fetcher;

import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.data.fetch.HttpResponse;
import at.cc.main.javawebcrawler.network.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlFetcherTest {
    private HttpClient httpClient;
    private HttpResponse response;
    private String body;
    private UrlFetcher urlFetcher;
    private String url;

    @BeforeEach
    void setup() {
        httpClient = mock(HttpClient.class);
        response = mock(HttpResponse.class);
        body = "<html>test</html>";

        urlFetcher = new UrlFetcher(httpClient);

        url = "https://aau.at";
    }

    @Test
    void successfulFetchResultOnStatus200() throws IOException {
        int statusCode = HttpURLConnection.HTTP_OK;

        when(httpClient.fetchUrl(url)).thenReturn(Optional.of(response));
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);

        FetchResult fetchResult = urlFetcher.fetchUrl(url);

        assertTrue(fetchResult.isSuccess());
        assertEquals(statusCode, fetchResult.getStatusCode());
        assertEquals(body, fetchResult.getBody());
        assertEquals(url, fetchResult.getUrl());
        assertNull(fetchResult.getErrorMsg());
        assertFalse(fetchResult.isBrokenUrl());
    }

    @Test
    void unsuccessfulFetchResultOnStatusNot200() throws IOException {
        int statusCode = 404;

        when(httpClient.fetchUrl(url)).thenReturn(Optional.of(response));
        when(response.statusCode()).thenReturn(statusCode);

        FetchResult fetchResult = urlFetcher.fetchUrl(url);

        assertFalse(fetchResult.isSuccess());
        assertEquals(statusCode, fetchResult.getStatusCode());
        assertNull(fetchResult.getBody());
        assertEquals(url, fetchResult.getUrl());
        assertEquals("Error fetching URL. StatusCode: " + statusCode, fetchResult.getErrorMsg());
        assertTrue(fetchResult.isBrokenUrl());
    }

    @Test
    void throwsIOExceptionOnGetUrlError() throws IOException {
        String errorMessage = "Could not connect to url";
        when(httpClient.fetchUrl(url)).thenThrow(new IOException(errorMessage));

        FetchResult fetchResult = urlFetcher.fetchUrl(url);

        assertFalse(fetchResult.isSuccess());
        assertNull(fetchResult.getBody());
        assertEquals(url, fetchResult.getUrl());
        assertEquals(errorMessage, fetchResult.getErrorMsg());
        assertTrue(fetchResult.isBrokenUrl());
    }

    @Test
    void unsuccessfulFetchResultWhenResponseIsEmpty() throws IOException {
        when(httpClient.fetchUrl(url)).thenReturn(Optional.empty());

        FetchResult fetchResult = urlFetcher.fetchUrl(url);

        assertFalse(fetchResult.isSuccess());
        assertNull(fetchResult.getBody());
        assertEquals(url, fetchResult.getUrl());
        assertEquals("Failed to fetch url: " + url, fetchResult.getErrorMsg());
        assertTrue(fetchResult.isBrokenUrl());
    }

}
