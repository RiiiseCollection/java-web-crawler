package at.cc.main.javawebcrawler.network;

import org.jsoup.Connection;

import java.io.IOException;

public interface HttpClient {
    Connection.Response getUrl(String url) throws IOException;
}
