package com.SearchEngine.WebCrawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CrawlerMain {
    static int maxNumOfCrawledUrls = 1000;
    static int totalNumofThreads = 100;

    public static void main(String[] args)  {
        //ConcurrentHashMap to store normalizeUrl as key and page content as  value
        //ConcurrentHashMap is more efficient than hashtable but not thread safe as it is not synchronized
        //,but we don't care if two threads access the same key at the same time
        //ConcurrentHashMap for visited urls
        ConcurrentHashMap<String, Boolean> visitedUrls = new ConcurrentHashMap<>();
        //ConcurrentHashMap for urls to be crawled
        ConcurrentHashMap<String, Boolean> urlsToCrawl = new ConcurrentHashMap<>();
        // store compact version of the crawled pages to avoid storing the whole page content again
        ConcurrentHashMap<String, String> compactStringOfPages = new ConcurrentHashMap<>();
        // controller object to access the database
        DBController controller = new DBController();
        DBController.connect();
        //  -------- load visitedUrls and compactStringOfPages from database --------
        controller.RetrieveCrawledUrls(visitedUrls, compactStringOfPages);
        // -------- load urlsToCrawl from database --------
        controller.RetrieveUrlsToCrawl(urlsToCrawl);
        // get number of crawled files
        int numOfCrawledPages = controller.getNumberofCrawledPages();
        //print the number of crawled files
        System.out.println("Number of crawled pages = " + numOfCrawledPages);
        //create array list of urls to be crawled to divide work among threads
        List<String> seedList = new ArrayList<>();
        seedList.add("https://edition.cnn.com");
        seedList.add("https://www.goal.com/en");
        seedList.add("https://www.bbc.com");
        seedList.add("https://www.encyclopedia.com");
        seedList.add("https://www.javatpoint.com");
        seedList.add("https://www.arduino.cc/en/hardware");
        seedList.add("https://www.geeksforgeeks.org/");
        seedList.add("https://www.python.org/");
        seedList.add("https://www.apple.com/");
        seedList.add("https://docs.python.org/");
        seedList.add("https://www.arduino.cc/en/hardware");
        seedList.add("https://www.geeksforgeeks.org/");
        seedList.add("https://www.python.org/");
        seedList.add("https://www.apple.com/");
        seedList.add("https://en.wikipedia.org/");


        if (urlsToCrawl.size() == 0) {//if cold start load the seed url
            for (String url : seedList) {
                urlsToCrawl.put(url, true);
            }
        }
        while (numOfCrawledPages < maxNumOfCrawledUrls) {
            numOfCrawledPages = controller.getNumberofCrawledPages();
            int remaining = maxNumOfCrawledUrls - numOfCrawledPages;
            if(urlsToCrawl.size() == 0) {
                System.out.println("No more URLs to crawl");
                break;
            }
            List<String> keyList = new ArrayList<>(urlsToCrawl.keySet());
            //send remaining urls to crawlWebPage function
            if(remaining < urlsToCrawl.size())
                seedList = keyList.subList(0, remaining);
            else    //send all urls to crawlWebPage function
                seedList = keyList;
            urlsToCrawl.clear();
            controller.deleteUrlsToCrawl();
            //print out the crawled pages
            System.out.println("Let's begin crawling...");
            WebCrawler.numThreads = totalNumofThreads;
            WebCrawler.seedList = seedList;
            System.out.println("Remaining: " + remaining + ", urlsToCrawlArray.length: " + seedList.size());
            WebCrawler.numUrls = Math.min(remaining, seedList.size());
            //print numUrls
            System.out.println("Number of URLs: " + WebCrawler.numUrls);
            WebCrawler.urlsToCrawl = urlsToCrawl;
            WebCrawler.visitedUrls = visitedUrls;
            WebCrawler.compactStringOfPages = compactStringOfPages;
            WebCrawler.controller = controller;
            //create number of threads
            Thread[] threads = new Thread[WebCrawler.numThreads];
            // Calculate the number of URLs per thread
            //int urlsPerThread = WebCrawler.numUrls / WebCrawler.numThreads;
            //int remainingUrls = WebCrawler.numUrls % WebCrawler.numThreads;
            //int startIndex = 0;
            for (int i = 0; i < WebCrawler.numThreads; i++) {               
                threads[i] = new Thread(new WebCrawler());
                threads[i].setName(i + "");
            }
            //start threads
            for (int i = 0; i < WebCrawler.numThreads; i++) {
                threads[i].start();
            }
            //wait for all threads to finish
            for (int i = 0; i < WebCrawler.numThreads; i++) {
                try {
                    threads[i].join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Threads Finished ------------------------------------------------");
            //print size of urlsToCrawl
            System.out.println( "total hyperlinks found: " + urlsToCrawl.size());
        }
    }
}

