package at.cc.main.javawebcrawler.network;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class JsoupHttpClient implements HttpClient {
    private static final int TIMEOUT_DELAY_MILLIS = 5000;

    @Override
    public Connection.Response getUrl(String url) throws IOException {
        if(url == null) return null;

        return Jsoup.connect(url)
                .timeout(TIMEOUT_DELAY_MILLIS)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .execute();
    }
}
