package at.cc.main.javawebcrawler.network;

import at.cc.main.javawebcrawler.data.fetch.HttpResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JsoupHttpClientTest {

    Connection.Response response;
    Connection connection;
    JsoupHttpClient client;

    @BeforeEach
    void setup() {
        response = mock(Connection.Response.class);
        connection = mock(Connection.class);
        client = new JsoupHttpClient();
    }

    @Test
    void correctlyReturnsResponseOnUrl() throws IOException {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect("https://aau.at"))
                    .thenReturn(connection);

            when(connection.timeout(anyInt())).thenReturn(connection);
            when(connection.followRedirects(anyBoolean())).thenReturn(connection);
            when(connection.ignoreHttpErrors(anyBoolean())).thenReturn(connection);
            when(connection.execute()).thenReturn(response);

            when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(response.body()).thenReturn("<html>test</html>");
            when(response.url()).thenReturn(URI.create("https://aau.at").toURL());

            Optional<HttpResponse> result = client.fetchUrl("https://aau.at");

            assertTrue(result.isPresent());
            assertEquals(HttpURLConnection.HTTP_OK, result.get().statusCode());
            assertEquals("<html>test</html>", result.get().body());
            assertEquals("https://aau.at", result.get().url());
        }
    }

    @Test
    void correctlyThrowOnUrlNull() {
        assertThrows(IllegalArgumentException.class, () -> client.fetchUrl(null));
    }

}
