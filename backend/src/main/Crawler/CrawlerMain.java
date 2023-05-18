
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class CrawlerMain {
    static int maxNumOfCrawledUrls = 6000;
    // static int totalNumofThreads = 20;

    public static void main(String[] args) {
        ConcurrentHashMap<String, Boolean> visitedUrls = new ConcurrentHashMap<>();

        ConcurrentHashMap<String, Boolean> urlsToCrawl = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> compactStringOfPages = new ConcurrentHashMap<>();

        CrawlerController controller = new CrawlerController();
        CrawlerController.connect();
        // Loads visitedUrls and compactStringOfPages from database --------
        controller.RetrieveCrawledUrls(visitedUrls, compactStringOfPages);
        // Loads urlsToCrawl from database --------
        controller.RetrieveUrlsToCrawl(urlsToCrawl);
        // Gets the number of crawled pages
        int numOfCrawledPages = controller.getNumberofCrawledPages();
        // Prints the number of crawled pages
        System.out.println("Number of crawled pages = " + numOfCrawledPages);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your the number of threads: ");
        int totalNumofThreads = scanner.nextInt();

        List<String> seedList = new ArrayList<>();

        String currentDirectory = System.getProperty("user.dir");

        // Specify the path to the file containing the URLs
        String seedPath = currentDirectory + "\\seedlist.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(seedPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                seedList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (urlsToCrawl.size() == 0) { // if cold start, load the seedList
            for (String url : seedList) {
                urlsToCrawl.put(url, true);
            }
        }

        // Deletes the files of the interrupted urls from the database
        String[] fileNames = controller.getAllFileNames();
        for (String fileName : fileNames) {
            String filePath = "\\HTMLdocs\\" + fileName;
            File file = new File(currentDirectory + filePath);
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    System.out.println("File deleted successfully: " + fileName);
                    // Deletes the file from the database
                    controller.deleteCurrentlyCrawlingPageByFileName(fileName);
                } else {
                    System.out.println("Failed to delete the file: " + fileName);
                }
            }
        }

        while (numOfCrawledPages < maxNumOfCrawledUrls) {
            numOfCrawledPages = controller.getNumberofCrawledPages();
            int remaining = maxNumOfCrawledUrls - numOfCrawledPages;
            if (urlsToCrawl.size() == 0) {
                System.out.println("No more URLs to crawl");
                break;
            }
            List<String> keyList = new ArrayList<>(urlsToCrawl.keySet());
            // Sends remaining urls to crawlWebPage function
            if (remaining < urlsToCrawl.size())
                seedList = keyList.subList(0, remaining);
            else // Send all urls to crawlWebPage function
                seedList = keyList;
            urlsToCrawl.clear();
            controller.deleteUrlsToCrawl();
            System.out.println("Let's begin crawling...");
            WebCrawler.seedList = seedList;
            System.out.println("Remaining: " + remaining + ", urlsToCrawlArray.length: " + seedList.size());
            WebCrawler.numUrls = Math.min(remaining, seedList.size());
            // System.out.println("Number of URLs: " + WebCrawler.numUrls);
            WebCrawler.urlsToCrawl = urlsToCrawl;
            WebCrawler.visitedUrls = visitedUrls;
            WebCrawler.compactStringOfPages = compactStringOfPages;
            WebCrawler.controller = controller;
            // Creates number of threads
            Thread[] threads = new Thread[totalNumofThreads];
            // Calculate the number of URLs per thread
            // int urlsPerThread = WebCrawler.numUrls / WebCrawler.numThreads;
            // int remainingUrls = WebCrawler.numUrls % WebCrawler.numThreads;
            for (int i = 0; i < totalNumofThreads; i++) {
                threads[i] = new Thread(new WebCrawler());
                threads[i].setName(String.valueOf(i));
            }
            // Starts threads
            for (int i = 0; i < totalNumofThreads; i++) {
                threads[i].start();
            }
            // wait for all threads to finish
            for (int i = 0; i < totalNumofThreads; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
            }
            System.out.println("Total hyperlinks found: " + urlsToCrawl.size());
        }
    }
}
