package at.cc.main.javawebcrawler.network;

import at.cc.main.javawebcrawler.data.fetch.HttpResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Optional;

public class JsoupHttpClient implements HttpClient {
    private static final int TIMEOUT_DELAY_MILLIS = 5000;
    private static final String TLS_PROVIDER = "TLS";
    private SSLContext unsafeSSLContext;
    private boolean isSSLFallbackAvailable = false;

    public JsoupHttpClient() {
        initUnsafeSSL();
    }

    @Override
    public Optional<HttpResponse> fetchUrl(String url) {
        if (url == null) throw new IllegalArgumentException("URL must not be null!");

        try {
            return Optional.of(toHttpResponse(fetchUrlDefault(url)));
        } catch (javax.net.ssl.SSLException e) {
            return trySSLFallback(url);
        } catch (IOException e) {
            System.err.println("Failed to fetch url: " + url);
            return Optional.empty();
        }
    }

    private Connection.Response fetchUrlDefault(String url) throws IOException {
        return Jsoup.connect(url)
                .timeout(TIMEOUT_DELAY_MILLIS)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .execute();
    }

    private Connection.Response fetchUrlWithoutCertificateCheck(String url, SSLContext context) throws IOException {
        return Jsoup.connect(url)
                .timeout(TIMEOUT_DELAY_MILLIS)
                .followRedirects(true)
                .sslContext(context)
                .ignoreHttpErrors(true)
                .execute();
    }

    private Optional<HttpResponse> trySSLFallback(String url) {
        if (!isSSLFallbackAvailable) return Optional.empty();

        try {
            return Optional.of(toHttpResponse(fetchUrlWithoutCertificateCheck(url, unsafeSSLContext)));
        } catch (IOException e) {
            System.err.println("SSL fallback failed for url: " + url);
            return Optional.empty();
        }
    }

    private void initUnsafeSSL() {
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

            SSLContext sslContext = SSLContext.getInstance(TLS_PROVIDER);
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            unsafeSSLContext = sslContext;
            isSSLFallbackAvailable = true;
        } catch (Exception e) {
            System.err.print("Failed to initialize unsafeSSLContext (Fallback unavailable)");
            isSSLFallbackAvailable = false;
        }
    }

    private HttpResponse toHttpResponse(Connection.Response response) {
        return new HttpResponse(response.statusCode(), response.body(), response.url().toString());
    }
}
