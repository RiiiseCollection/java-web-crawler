package at.cc.main.javawebcrawler.network;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

            client = new JsoupHttpClient();

            Connection.Response result = client.getUrl("https://aau.at");

            assertEquals(response, result);
        }
    }

    @Test
    void correctlyReturnNullOnUrlNull() throws IOException {
        Connection.Response result = client.getUrl(null);

        assertNull(result);
    }

}
