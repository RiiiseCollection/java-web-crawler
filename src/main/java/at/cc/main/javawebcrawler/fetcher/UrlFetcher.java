package at.cc.main.javawebcrawler.fetcher;

import at.cc.main.javawebcrawler.data.FetchResult;
import at.cc.main.javawebcrawler.network.HttpClient;
import at.cc.main.javawebcrawler.network.JsoupHttpClient;
import org.jsoup.Connection;

import java.io.IOException;

public class UrlFetcher {
    private final HttpClient httpClient;

    public UrlFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public FetchResult fetchUrl(String url) {
        FetchResult fetchResult = new FetchResult(url);

        try {
            Connection.Response response = httpClient.getUrl(url);

            if (response == null) {
                fetchResult.setSuccess(false);
                fetchResult.setErrorMsg("Provided url was null");
            } else {
                fetchResult.setStatusCode(response.statusCode());

                if (fetchResult.getStatusCode() == 200) {
                    fetchResult.setDocument(response.parse());
                    fetchResult.setSuccess(true);
                } else {
                    fetchResult.setSuccess(false);
                    fetchResult.setErrorMsg("Error fetching Url. StatusCode: " + fetchResult.getStatusCode());
                }
            }
        } catch (IOException e) {
            fetchResult.setSuccess(false);
            fetchResult.setErrorMsg(e.getMessage());
        }

        return fetchResult;
    }

}
