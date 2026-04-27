package at.cc.main.javawebcrawler.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class UrlUtil {

    public static String getDomain(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch(URISyntaxException e) {
            return null;
        }
    }

    public static boolean isAllowedDomain(String url, List<String> allowedDomains) {
        String host = getDomain(url);
        if (host == null) {
            return false;
        }

        String cleanHost = host.startsWith("www.") ? host.substring(4) : host;

        for (String allowedDomain : allowedDomains) {
            String cleanDomain = allowedDomain.startsWith("www.") ?
                    allowedDomain.substring(4) : allowedDomain;

            if (cleanHost.equals(cleanDomain) || cleanHost.endsWith("." + cleanDomain)) {
                return true;
            }
        }
        return false;
    }
}
