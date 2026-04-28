package at.cc.main.javawebcrawler.network;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;

public class JsoupHttpClient implements HttpClient {
    private static final int TIMEOUT_DELAY_MILLIS = 5000;

    @Override
    public Connection.Response getUrl(String url) throws IOException {
        if (url == null) return null;

        try {
            return fetchUrl(url, null);
        } catch (javax.net.ssl.SSLException e) {
            SSLContext context = initUnsafeSSL();

            if (context == null) {
                return null;
            }

            return fetchUrl(url, context);
        }
    }

    private Connection.Response fetchUrl(String url, SSLContext context) throws IOException {
        if (context == null) {
            return Jsoup.connect(url)
                    .timeout(TIMEOUT_DELAY_MILLIS)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .execute();
        }

        return Jsoup.connect(url)
                .timeout(TIMEOUT_DELAY_MILLIS)
                .followRedirects(true)
                .sslContext(context)
                .ignoreHttpErrors(true)
                .execute();
    }

    private SSLContext initUnsafeSSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);

            return sslContext;
        } catch (Exception e) {
            return null;
        }
    }
}
