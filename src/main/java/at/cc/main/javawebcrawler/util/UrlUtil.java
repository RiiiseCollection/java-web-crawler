package at.cc.main.javawebcrawler.util;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlUtil {

    public static String getDomain(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch(URISyntaxException e) {
            return null;
        }
    }
}
