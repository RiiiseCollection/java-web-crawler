package at.cc.main.javawebcrawler.network;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;

public class JsoupHttpClient implements HttpClient {
    private static final int TIMEOUT_DELAY_MILLIS = 5000;
    private static final String TLS_PROVIDER = "TLS";
    private final SSLContext unsafeSSLContext;

    public JsoupHttpClient() {
        this.unsafeSSLContext = initUnsafeSSL();
    }

    @Override
    public Connection.Response fetchUrl(String url) throws IOException {
        if (url == null) return null;

        try {
            return fetchUrlDefault(url);
        } catch (javax.net.ssl.SSLException e) {
            if (unsafeSSLContext == null) return null;

            return fetchUrlWithoutCertificateCheck(url, unsafeSSLContext);
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

            SSLContext sslContext = SSLContext.getInstance(TLS_PROVIDER);
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return sslContext;
        } catch (Exception e) {
            return null;
        }
    }
}
