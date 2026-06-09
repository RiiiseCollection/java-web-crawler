package at.cc.main.javawebcrawler.util;

import java.net.URLConnection;

public class UrlValidator {
    public static boolean isHtmlUrl(String url) {
        if (url == null) return false;

        String contentType = URLConnection.guessContentTypeFromName(url);
        return contentType == null || contentType.startsWith("text/html");
    }
}
