package at.cc.main.javawebcrawler.fetcher;

import at.cc.main.javawebcrawler.data.FetchResult;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class UrlFetcher {
    private static final int TIMEOUT_DELAY_MILLIS = 5000;

    public FetchResult fetchUrl(String url) {
        FetchResult fetchResult = new FetchResult(url);

        try {
            Connection conn = Jsoup.connect(url)
                    .timeout(TIMEOUT_DELAY_MILLIS)
                    .followRedirects(true)
                    .ignoreHttpErrors(true);

            Connection.Response response = conn.execute();

            fetchResult.setStatusCode(response.statusCode());

            if(fetchResult.getStatusCode() == 200) {
                fetchResult.setDocument(response.parse());
                fetchResult.setSuccess(true);
            } else {
                fetchResult.setSuccess(false);
                fetchResult.setErrorMsg("Error fetching Url. StatusCode: " + fetchResult.getStatusCode());
            }

        } catch (IOException e) {
            fetchResult.setSuccess(false);
            fetchResult.setErrorMsg(e.getMessage());
        }

        return fetchResult;
    }

}
