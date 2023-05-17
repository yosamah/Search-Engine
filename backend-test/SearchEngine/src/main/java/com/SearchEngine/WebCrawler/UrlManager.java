//package com.SearchEngine.WebCrawler;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//
//import crawlercommons.robots.BaseRobotRules;
//import crawlercommons.robots.SimpleRobotRules;
//import crawlercommons.robots.SimpleRobotRulesParser;
//
//
//public class UrlManager {
//    public static ConcurrentHashMap<String, BaseRobotRules> robotRulesCache = new ConcurrentHashMap<>();
//
//    // public static boolean isAllowedByRobotsTxt(String url) throws URISyntaxException, IOException {
//    //     try {
//    //         URL urlObj = new URL(url);
//    //         String host = urlObj.getHost();
//
//    //         if (robotRulesCache.containsKey(host)) {
//    //             BaseRobotRules rules = robotRulesCache.get(host);
//    //             return rules== null || (rules != null && rules.isAllowed(urlObj.toString()));
//    //         } else {
//    //             String robotsTxtUrl = new URL(urlObj.getProtocol(), host, "/robots.txt").toString();
//
//    //             Document doc = null;
//    //             try {
//    //                 doc = Jsoup.connect(robotsTxtUrl).get();
//    //             } catch (IOException e) {
//    //                 System.out.println(Thread.currentThread().getName() + ": Error while crawling page: " + url);
//    //             }// catch malformed url
//    //             catch (Exception e) {
//    //                 System.out.println(Thread.currentThread().getName() + ": Malformed URL: " + url);
//    //             }
//    //             String robotsTxt = doc.body().text();
//    //             StringBuilder sb = new StringBuilder();
//    //             String[] lines = robotsTxt.split("\n");
//
//    //             for (String line : lines) {
//    //                 line = line.trim();
//    //                 if (!line.isEmpty()) {
//    //                     if (line.startsWith("User-agent:") && sb.length() > 0) {
//    //                         sb.append("\n");
//    //                     }
//    //                     sb.append(line).append("\n");
//    //                 }
//    //             }
//
//    //             // // for each two white space in the robots.txt file add a new line
//    //             // String[] lines = robotsTxt.split("\\s+");
//    //             // StringBuilder sb = new StringBuilder();
//    //             // for (int i = 0; i < lines.length; i+=2) {
//    //             //     if (i < lines.length - 1) {
//    //             //         sb.append(lines[i] +" "+lines[i+1] + "\n");
//    //             //     }
//    //             //     else {
//    //             //         break;
//    //             //     }
//    //             // }
//
//    //             robotsTxt = sb.toString();
//    //             //System.out.println("Robots.txt:" + robotsTxt);
//    //             // convert the robots.txt file to raw bytes
//    //             byte[] robotsTxtBytes = robotsTxt.getBytes();
//
//    //             SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
//    //             String contentType = "text/plain";
//    //             BaseRobotRules rules = parser.parseContent(contentType, robotsTxtBytes, host, url);
//
//    //             robotRulesCache.put(host, rules);
//
//    //             return rules == null || (rules != null && rules.isAllowed(urlObj.toString()));
//    //         }
//    //     } catch (IOException e) {
//    //         //e.printStackTrace();
//    //         System.out.println("Error in isAllowedByRobotsTxt");
//    //     }
//    //     return true;
//    // }
//
//
//    public static boolean isAllowedByRobotsTxt(String url) throws URISyntaxException, IOException {
//        try {
//            URL urlObj = new URL(url);
//            String host = urlObj.getHost();
//
//            if (robotRulesCache.containsKey(host)) {
//                BaseRobotRules rules = robotRulesCache.get(host);
//                return rules == null || (rules != null && rules.isAllowed(urlObj.toString()));
//            } else {
//                String robotsTxtUrl = new URL(urlObj.getProtocol(), host, "/robots.txt").toString();
//
//                Document doc = null;
//                try {
//                    doc = Jsoup.connect(robotsTxtUrl).get();
//                } catch (IOException e) {
//                    System.out.println(Thread.currentThread().getName() + ": Error while crawling page: " + url);
//                    return true; // or false depending on your desired behavior
//                } catch (Exception e) {
//                    System.out.println(Thread.currentThread().getName() + ": Malformed URL: " + url);
//                    return true; // or false depending on your desired behavior
//                }
//
//                if (doc == null) {
//                    System.out.println(Thread.currentThread().getName() + ": Error while crawling page: " + url);
//                    return true; // or false depending on your desired behavior
//                }
//
//                String robotsTxt = doc.body().text();
//                StringBuilder sb = new StringBuilder();
//                String[] lines = robotsTxt.split("\n");
//
//                for (String line : lines) {
//                    line = line.trim();
//                    if (!line.isEmpty()) {
//                        if (line.startsWith("User-agent:") && sb.length() > 0) {
//                            sb.append("\n");
//                        }
//                        sb.append(line).append("\n");
//                    }
//                }
//
//                robotsTxt = sb.toString();
//                byte[] robotsTxtBytes = robotsTxt.getBytes();
//
//                SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
//                String contentType = "text/plain";
//                BaseRobotRules rules = parser.parseContent(contentType, robotsTxtBytes, host, url);
//
//                robotRulesCache.put(host, rules);
//
//                return rules == null || (rules != null && rules.isAllowed(urlObj.toString()));
//            }
//        } catch (IOException e) {
//            System.out.println("Error in isAllowedByRobotsTxt");
//        }
//        return true;
//    }
//
//    public static String compactString(String text) {
//        text = text.trim(); //Removes leading and trailing spaces
//        text = text.toLowerCase(); //Converts to lowercase
//        text = text.replaceAll("\\s+", ""); //Removes extra spaces
//
//        if (text.length() <= 100) {
//            return text;
//        } else {
//            String compactText = text.substring(0, 100);
//            return compactText;
//        }
//    }
//
//    public static String urlNormalizer(String url) throws URISyntaxException {
//        URI uri = new URI(url);
//
//        // Normalize the URL components
//        String scheme = uri.getScheme().toLowerCase();
//        String host = uri.getHost().toLowerCase().trim();
//        int port = uri.getPort();
//        String path = uri.getPath().replaceAll("/+", "/"); // Normalize path by removing redundant slashes
//        String query = uri.getQuery();
//        String fragment = uri.getFragment();
//
//        // Reconstruct the normalized URL
//        StringBuilder normalizedUrl = new StringBuilder();
//        normalizedUrl.append(scheme).append("://").append(host);
//        if (port > 0) {
//            normalizedUrl.append(":").append(port);
//        }
//        normalizedUrl.append(path);
//        if (query != null) {
//            normalizedUrl.append("?").append(query);
//        }
//        if (fragment != null) {
//            normalizedUrl.append("#").append(fragment);
//        }
//        return normalizedUrl.toString();
//    }
//}