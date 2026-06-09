package at.cc.main.javawebcrawler.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public class DomainValidator {

    public static Optional<String> extractDomain(String url) {
        try {
            URI uri = new URI(url);
            return Optional.of(uri.getHost());
        } catch (URISyntaxException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public static boolean isAllowedDomain(String url, List<String> allowedDomains) {
        Optional<String> hostOptional = extractDomain(url);

        if (hostOptional.isEmpty()) return false;

        String host = hostOptional.get();

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
