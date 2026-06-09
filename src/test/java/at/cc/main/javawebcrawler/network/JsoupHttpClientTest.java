package at.cc.main.javawebcrawler.network;

import at.cc.main.javawebcrawler.data.fetch.HttpResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JsoupHttpClientTest {

    private static final String TEST_URL = "https://aau.at";
    private static final String TEST_RESPONSE_BODY = "<html>test</html>";

    Connection.Response response;
    Connection defaultConnection;
    Connection fallbackConnection;
    JsoupHttpClient client;

    @BeforeEach
    void setup() throws MalformedURLException {
        response = mock(Connection.Response.class);
        defaultConnection = mock(Connection.class);
        fallbackConnection = mock(Connection.class);
        client = new JsoupHttpClient();

        when(defaultConnection.timeout(anyInt())).thenReturn(defaultConnection);
        when(defaultConnection.followRedirects(anyBoolean())).thenReturn(defaultConnection);
        when(defaultConnection.ignoreHttpErrors(anyBoolean())).thenReturn(defaultConnection);

        when(fallbackConnection.timeout(anyInt())).thenReturn(fallbackConnection);
        when(fallbackConnection.followRedirects(anyBoolean())).thenReturn(fallbackConnection);
        when(fallbackConnection.ignoreHttpErrors(anyBoolean())).thenReturn(fallbackConnection);
        when(fallbackConnection.sslContext(any())).thenReturn(fallbackConnection);

        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(response.body()).thenReturn(TEST_RESPONSE_BODY);
        when(response.url()).thenReturn(URI.create(TEST_URL).toURL());
    }

    @Test
    void correctlyReturnResponseOnUrl() throws IOException {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(TEST_URL))
                    .thenReturn(defaultConnection);
            when(defaultConnection.execute()).thenReturn(response);

            Optional<HttpResponse> result = client.fetchUrl(TEST_URL);

            assertTrue(result.isPresent());
            assertEquals(HttpURLConnection.HTTP_OK, result.get().statusCode());
            assertEquals(TEST_RESPONSE_BODY, result.get().body());
            assertEquals(TEST_URL, result.get().url());
        }
    }

    @Test
    void correctlyReturnEmptyOnSSLExceptionIfFallbackUnavailable() throws IOException, NoSuchFieldException, IllegalAccessException {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(TEST_URL))
                    .thenReturn(defaultConnection);
            when(defaultConnection.execute()).thenThrow(new SSLException("SSL Exception"));

            Field field = JsoupHttpClient.class.getDeclaredField("isSSLFallbackAvailable");
            field.setAccessible(true);
            field.set(client, false);

            Optional<HttpResponse> result = client.fetchUrl(TEST_URL);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void correctlyReturnResponseOnSSLExceptionIfFallbackAvailable() throws IOException {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(TEST_URL))
                    .thenReturn(defaultConnection)
                    .thenReturn(fallbackConnection);
            when(defaultConnection.execute()).thenThrow(new SSLException("SSL Exception"));
            when(fallbackConnection.execute()).thenReturn(response);

            Optional<HttpResponse> result = client.fetchUrl(TEST_URL);

            assertTrue(result.isPresent());
            assertEquals(HttpURLConnection.HTTP_OK, result.get().statusCode());
            assertEquals(TEST_RESPONSE_BODY, result.get().body());
            assertEquals(TEST_URL, result.get().url());
            verify(fallbackConnection).sslContext(any());
        }
    }

    @Test
    void correctlyReturnEmptyOnFallbackFailed() throws IOException {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(TEST_URL))
                    .thenReturn(defaultConnection)
                    .thenReturn(fallbackConnection);
            when(defaultConnection.execute()).thenThrow(new SSLException("SSL Exception"));
            when(fallbackConnection.execute()).thenThrow(new IOException("Fallback IO Exception"));

            Optional<HttpResponse> result = client.fetchUrl(TEST_URL);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void correctlyReturnEmptyOnIOException() throws IOException {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(TEST_URL))
                    .thenReturn(defaultConnection);
            when(defaultConnection.execute()).thenThrow(new IOException("IO Exception"));

            Optional<HttpResponse> result = client.fetchUrl(TEST_URL);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void correctlyReturnEmptyOnIllegalArgumentException() throws IOException {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(TEST_URL))
                    .thenReturn(defaultConnection);
            when(defaultConnection.execute()).thenThrow(new IllegalArgumentException("IllegalArgument Exception"));

            Optional<HttpResponse> result = client.fetchUrl(TEST_URL);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void correctlyThrowOnUrlNull() {
        assertThrows(IllegalArgumentException.class, () -> client.fetchUrl(null));
    }

}
