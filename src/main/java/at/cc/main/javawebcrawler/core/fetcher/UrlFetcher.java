package at.cc.main.javawebcrawler.core.fetcher;

import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.network.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UrlFetcher {
    private static final Logger log = LoggerFactory.getLogger(UrlFetcher.class);

    private final HttpClient httpClient;

    public UrlFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public FetchResult fetchUrl(String url) {
        FetchResult fetchResult = new FetchResult(url);

        try {
            httpClient.fetchUrl(url).ifPresentOrElse(
                    response -> {
                        fetchResult.setStatusCode(response.statusCode());

                        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                            fetchResult.setBody(response.body());
                            fetchResult.setSuccess(true);
                        } else {
                            fetchResult.setSuccess(false);
                            fetchResult.setErrorMsg("Error fetching URL. StatusCode: " + fetchResult.getStatusCode());
                            log.warn("Non-200 response for {}: status {}", url, response.statusCode());
                        }
                    },
                    () -> {
                        fetchResult.setSuccess(false);
                        fetchResult.setErrorMsg("Failed to fetch url: " + url);
                        log.warn("Empty response for url: {}", url);
                    }
            );
        } catch (IOException e) {
            fetchResult.setSuccess(false);
            fetchResult.setErrorMsg(e.getMessage());
            log.error("IOException while fetching {}: {}", url, e.getMessage());
        }

        return fetchResult;
    }

}
