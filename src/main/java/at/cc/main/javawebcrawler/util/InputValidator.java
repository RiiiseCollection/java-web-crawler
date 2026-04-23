package at.cc.main.javawebcrawler.util;

import at.cc.main.javawebcrawler.exception.InputValidationException;

import java.net.URI;
import java.net.URISyntaxException;

public class InputValidator {

    private static final String DOMAIN_REGEX =
            "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";

    public static void validateInput(String[] args) {
        if (args.length < 3) {
            throw new InputValidationException("Usage: java -jar crawler.jar url depth domain1 domain2 ...");
        }

        if (!isValidUrlSyntax(args[0])) {
            throw new InputValidationException("Please provide a valid url as the 1st argument");
        }

        if (!isNumber(args[1])) {
            throw new InputValidationException("Please provide a Number as the 2nd argument");
        }

        int domainOffset = 2;
        for (int i = domainOffset; i < args.length; i++) {
            if (!isValidDomainSyntax(args[i])) {
                throw new InputValidationException("Domain at position " + i + " of arguments is not valid");
            }
        }
    }

    private static boolean isValidUrlSyntax(String url) {
        try {
            URI uri = new URI(url);
            return uri.getScheme() != null &&
                    (uri.getScheme().equals("http") || uri.getScheme().equals("https")) &&
                    uri.getHost() != null && !uri.getHost().isBlank();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static boolean isNumber(String number) {
        if (number == null) return false;

        try {
            return Integer.parseInt(number) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidDomainSyntax(String domain) {
        return domain != null && domain.matches(DOMAIN_REGEX);
    }

}
