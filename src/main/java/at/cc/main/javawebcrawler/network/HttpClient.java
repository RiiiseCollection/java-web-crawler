package at.cc.main.javawebcrawler.network;

import at.cc.main.javawebcrawler.data.fetch.HttpResponse;

import java.io.IOException;
import java.util.Optional;

public interface HttpClient {
    Optional<HttpResponse> fetchUrl(String url) throws IOException;
}
