package at.cc.main.javawebcrawler.core.fetcher;

import at.cc.main.javawebcrawler.data.fetch.FetchResult;
import at.cc.main.javawebcrawler.network.HttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UrlFetcher {
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
                        }
                    },
                    () -> {
                        fetchResult.setSuccess(false);
                        fetchResult.setErrorMsg("Failed to fetch url: " + url);
                    }
            );
        } catch (IOException e) {
            fetchResult.setSuccess(false);
            fetchResult.setErrorMsg(e.getMessage());
        }

        return fetchResult;
    }

}
