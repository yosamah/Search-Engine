package WebCrawler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import crawlercommons.robots.BaseRobotRules;

public class UrlManager {
    public static ConcurrentHashMap<String, BaseRobotRules> robotRulesCache = new ConcurrentHashMap<>();
    
    public static boolean isAllowedByRobotsTxt(String url) throws URISyntaxException, IOException {
        try {
            URL urlObj = new URL(url);
            String host = urlObj.getHost();

            if (robotRulesCache.containsKey(host)) {
                BaseRobotRules rules = robotRulesCache.get(host);
                return rules == null || (rules != null && rules.isAllowed(urlObj.toString()));
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error in isAllowedByRobotsTxt");
            return false;
        }
    }

    public static String compactString(String text) {
        text = text.trim(); //Removes leading and trailing spaces
        text = text.toLowerCase(); //Converts to lowercase
        text = text.replaceAll("\\s+", ""); //Removes extra spaces

        if (text.length() <= 100) {
            return text;
        } else {
            Random random = new Random();
            int startIndex = random.nextInt(text.length() - 100 + 1);
            String compactText = text.substring(startIndex, startIndex + 100);
            return compactText;
        }
    }

    public static String urlNormalizer(String url) throws URISyntaxException {
        URI uri = new URI(url);

        // Normalize the URL components
        String scheme = uri.getScheme().toLowerCase();
        String host = uri.getHost().toLowerCase().trim();
        int port = uri.getPort();
        String path = uri.getPath().replaceAll("/+", "/"); // Normalize path by removing redundant slashes
        String query = uri.getQuery();
        //String fragment = uri.getFragment();

        // Reconstruct the normalized URL
        StringBuilder normalizedUrl = new StringBuilder();
        normalizedUrl.append(scheme).append("://").append(host);
        if (port > 0) {
            normalizedUrl.append(":").append(port);
        }
        normalizedUrl.append(path);
        if (query != null) {
            normalizedUrl.append("?").append(query);
        }
        // if (fragment != null) {
        //     normalizedUrl.append("#").append(fragment);
        // }
        return normalizedUrl.toString();
    }
}