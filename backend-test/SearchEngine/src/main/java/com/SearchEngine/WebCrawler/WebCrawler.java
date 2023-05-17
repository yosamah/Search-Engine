package com.SearchEngine.WebCrawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tika.langdetect.optimaize.OptimaizeLangDetector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler implements Runnable {
    public static int numThreads;
    public static int numUrls;
    public static List<String> seedList;
    public static ConcurrentHashMap<String, Boolean> visitedUrls;
    public static ConcurrentHashMap<String, Boolean> urlsToCrawl;
    public static ConcurrentHashMap<String, String> compactStringOfPages;
    int counter = 0;
  
    public static DBController controller;

    OptimaizeLangDetector langDetector;
    public void run() {
        langDetector = new OptimaizeLangDetector(); // Create language detector instance
        langDetector.loadModels(); // Load language detection models
        // get the thread number
        int threadNum = Integer.parseInt(Thread.currentThread().getName());
        // get the start and end index for the current thread
        System.out.println("Thread " + threadNum + "is currently running");

        try {
            crawlWebPage(seedList, visitedUrls, urlsToCrawl, compactStringOfPages);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


    //function to crawl a webpage pass visited urls and urls to be crawled
    public void crawlWebPage(List<String> seedList,
                                    ConcurrentHashMap<String, Boolean> visitedUrls,
                                    ConcurrentHashMap<String, Boolean> urlsToCrawl,
                                    ConcurrentHashMap<String,String> compactStringOfPages) throws IOException, URISyntaxException {
        String url = null;
        while (!seedList.isEmpty()) {
            synchronized (seedList) {
                if (seedList.size() > 0)
                    url = seedList.remove(0);
            }
            try {
                //if interrupted, be started again to crawl the documents on the list without revisiting documents that have been previously downloaded.
                if (!visitedUrls.containsKey(url)) {
                    System.out.println(Thread.currentThread().getName() + " is currently crawling page: " + url);
                    //Checks if url is allowed to be crawled by robots.txt
                    if (!UrlManager.isAllowedByRobotsTxt(url)) {
                        System.out.println(Thread.currentThread().getName() + "-> Disallowed by robots.txt: " + url);
                        //Adds url to visitedUrls
                        visitedUrls.put(url, true);
                        //controller.createNewPageWithoutFilePath(url);
                        continue;
                    }
                    //create a document object
                    Document doc = null;
                    try {
                            doc = Jsoup.connect(url).get();
                        } catch (IOException e) {
                            System.out.println(Thread.currentThread().getName() + ": Error while crawling page: " + url);
                            //Adds url to visitedUrls
                            visitedUrls.put(url, true);
                            //controller.createNewPageWithoutFilePath(url);
                            continue;
                        }
                        catch (Exception e) {
                            System.out.println(Thread.currentThread().getName() + ": Malformed URL: " + url);
                            //Adds url to visitedUrls
                            visitedUrls.put(url, true);
                            //controller.createNewPageWithoutFilePath(url);
                            continue;
                        }
                    //chack doc html lang attribute with jsoup and if not english, skip
                    if(!isEnglishLanguage(doc)) {
                        System.out.println(Thread.currentThread().getName() + "-> Non-english page: " + url);
                        //Adds url to visitedUrls
                        visitedUrls.put(url, true);
                        //controller.createNewPageWithoutFilePath(url);
                        continue;
                    }
                    String compactString = UrlManager.compactString(doc.body().text());
                    //checks if compact String is already in database
                    if (compactStringOfPages != null && compactStringOfPages.containsKey(compactString)) {
                        //Adds url to visitedUrls
                        //visitedUrls.put(url, true);
                        //controller.createNewPageWithoutFilePath(url);
                        System.out.println(Thread.currentThread().getName() + "-> Compact String exists: " + url);
                        continue;
                    }
                    String currentDirectory = System.getProperty("user.dir");
                    String fileName = counter + Thread.currentThread().getName();
                    String filePath = "\\HTMLdocs\\" + fileName + ".html";
                    counter++;
                    File HTMLFile = new File(currentDirectory + filePath);
                    HTMLFile.createNewFile();
                    try {
                        FileWriter fw = new FileWriter(HTMLFile);
                        fw.write(doc.html());
                        fw.close();
                    } catch (IOException e) {
                        System.out.println(Thread.currentThread().getName() + ": Error while writing to file: " + url);
                        break;
                    }
                    //Adds compact String to compactStringOfPages
                    compactStringOfPages.put(compactString, url);
                    //Adds url to visitedUrls
                    visitedUrls.put(url, true);
                    controller.createNewPage(url, filePath, compactString);
                   
                    //Extracts all hyperlinks from Document
                    Elements hyperlinks = doc.select("a[href]");
                    System.out.println( Thread.currentThread().getName() + ": Found " + hyperlinks.size() + " hyperlinks");
                    for (Element hl : hyperlinks) {
                        String normalizedUrl = hl.attr("abs:href");
                        try {
                                //Normalizes the link
                                normalizedUrl = UrlManager.urlNormalizer(normalizedUrl);
                                //System.out.println(Thread.currentThread().getName() + "-> Normalized URL: " + normalizedUrl);
                        } catch (URISyntaxException e) {
                                System.out.println(Thread.currentThread().getName() + ": Malformed URL: " + normalizedUrl);
                                continue;
                        }
                        //check if link is not visited
                        if (!visitedUrls.containsKey(normalizedUrl)) {
                            //add link to urls to be crawled if not exists
                            if(!urlsToCrawl.containsKey(normalizedUrl)) {
                                //System.out.println(Thread.currentThread().getName() + " is adding " + normalizedUrl + " to urls to be crawled");
                                urlsToCrawl.put(normalizedUrl, true);
                                controller.createNewFuturePage(normalizedUrl);
                            }
                        }
                    }
                    
                }
            } catch (Exception e) {
               continue;
            }
        }
    }

    //detects language of the document
    public boolean isEnglishLanguage(Document doc) {
        try {
            String text = doc.text(); // Extract text content from HTML document
            return langDetector.detect(text).getLanguage().equals("en"); // Perform language detection
        } catch (Exception e) {
            e.printStackTrace();
            return false;     //If error occurred, assume it's not English
        }
    }
    

}
