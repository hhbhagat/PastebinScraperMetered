package pastebin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import org.jsoup.nodes.Document;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

/*TODO:
 * /1. Comment code
 * /2. Refactor out to methods
 * /3. Implement object to contain paste URLs
 * /4. Integrate raw-paste cleaning into Paste classes
 * /TODO LATER 4. Differentiate between expiring pastes and forever pastes and separate accordingly 
 * 5. Implement scanning queue
 * 6. Implement timer to prevent site lockout
 * 
 * PROSPECTIVE CHANGES TO MAIN: 
 *      -Make ArrayList of VolatilePaste and InvolatilePaste
 * 
 * -----------------TODO Classes--------------------------------------
 *  1.  -VolatilePaste class 
 *          -properties:
 *              -String: Paste URL
 *              -String: Paste Title
 *              -String: Paste Type
 *              -String: Raw paste url
 *              -Integer: Expiry time
 *              -Long: Expiry time in terms of epoch
 *          -methods
 *              -Getters
 *                  -URL - getURL (raw paste URL)
 *                  -String - getType (paste type)
 *                  -String - getTitle (paste Title)
 *                  -int - getExpiry
 *                  -long - getExpiry_Epoch (Get the expiry in terms of the epoch)
 *              -Setters
 *                  -void - setURL (raw paste URL)
 *                  -void - setType (paste type)
 *                  -void - setTitle (paste Title)
 *                  -void - setExpiry
 *                  -void - setExpiry_Epoch (Get the expiry in terms of the epoch)
 *  2.  -InvolatilePaste class
 *          -properties
 *              -String: Paste URL
 *              -String: Paste Title
 *              -String: Paste Type
 *              -String: Raw paste url
 *          -methods
 *              -Getters
 *                  -String - getURL (raw paste URL)
 *                  -String - getType (paste type)
 *                  -String - getTitle (paste Title)
 *              -Setters
 *                  -String - setURL (raw paste URL)
 *                  -String - setType (paste type)
 *                  -String - setTitle (paste Title)
 * --------------------------------------------------------------------
 */
public class Pastebin {

    public static Document doc;
    public static Document temp;
    public static Elements list;
    public static File file;
    public static int ptime;
    private static ArrayList<InvolatilePaste> invPastes = new ArrayList<InvolatilePaste>();
    private static ArrayList<VolatilePaste> volPastes = new ArrayList<VolatilePaste>();
    private static ArrayList<Paste> genericPastes = new ArrayList<Paste>();
    private static ArrayList<Paste> diffGenericPastes = new ArrayList<Paste>();
    private static PastePool pool = new PastePool();
    private static Lock blockedLock = new ReentrantLock();
    private static ArrayList<Thread> threadArr = new ArrayList<Thread>();
    private static boolean firstRun = true;
    private static CountDownLatch cdlatch = new CountDownLatch(1);

//pastebin archive has 250 recent paste capacity
    public static void main(String[] args) throws Exception {

        System.out.println("Specify scrape interval (In Minutes)");
        Scanner input1 = new Scanner(System.in);
        ptime = input1.nextInt();

        ptime = ptime * 60000;

        //Main program logic

        while (true) {

            scanThis("http://pastebin.com/archive/"); //scans the archive and gets the list.

            try {

                list = doc.select(".maintable"); //gets element of list of pastes

                Elements filteredList_tr = list.select("tr"); //Gets the hrefs of pastes from the list

                //starting at one because of the first table element being garbage
                for (int i = 1; i < filteredList_tr.size(); i++) { //adds them all to RawHREF
                    Paste tempObj = new InvolatilePaste(); //involatile paste fallback
                    String type = filteredList_tr.select("td[align]").get(0).select("a[href]").get(0).text();
                    tempObj.setType(type);

                    //gets url from href in td and cleans it
                    String url = filteredList_tr.get(i).select("td").get(0).select("a[href]").attr("href").replace("/", "");

                    tempObj.setURL("http://pastebin.com/raw.php?i=" + url);

                    String title = filteredList_tr.get(i).select("td").get(0).select("a[href]").text();
                    title = title.replace("\\", "_");
                    title = title.replace("/", "_");
                    title = title.replace(":", "_");
                    title = title.replace("*", "_star_");
                    title = title.replace("?", "_qmark_");
                    title = title.replace("<", "_leftcaret_");
                    title = title.replace(">", "_rightcaret_");
                    title = title.replace("|", "_pipe_");
                    tempObj.setTitle(title);
                    genericPastes.add(tempObj);

                    for (int z = 0; z < genericPastes.size() - 1; z++) {//check for dupes
                        if (genericPastes.get(genericPastes.size() - 1).getURL().toString().equals(genericPastes.get(z).getURL().toString())) {
                            genericPastes.remove(genericPastes.size() - 1);
                            break;
                        }
                    }

                }
            } catch (Exception e) {
                for (StackTraceElement a : e.getStackTrace()) {
                    System.out.println(a);
                }
                System.out.println(e.getMessage() + "Sorry, the site had blocked you.");
            }

            if (firstRun) {
                startDownloadThread();
                firstRun = false;
            }

            if (cdlatch.getCount() == 1) {
                System.out.println("Resuming thread.");

                Runnable r = new Runnable() {
                    public void run() {
                        Pastebin.resumeThread();
                    }
                };
                new Thread(r).start();
                
            }

            System.out.println("Now Pausing for " + ptime / 60000 + " Mins....");

            addToPool();
            genericPastes.clear();
            cdlatch = new CountDownLatch(1);
            System.out.println("Cleared paste cache.");

            ptime = 10 * 60000;

            Thread.sleep(ptime);
        }

    }

    private static Document scanThis(String archive) {

        try {
            System.out.println("Getting");
            doc = Jsoup.connect(archive).userAgent("Mozilla").get();
            System.out.println("Getting Finished");
        } catch (Exception e) {
            System.out.println("Sorry, you'll have to wait some time because the site blocked you");
            System.out.println("Waiting for ...." + "31" + " minutes");
            try {
                Thread.sleep(1900000);
            } catch (Exception f) {
                System.out.println("Time delay failed");
            }
            //System.out.println("Error: " + e);
        }

        return doc;

    }

    public static void writeToFile(String input) throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter("L:\\a.txt"));
        out.write(input + "\n");
        out.close();
    }

    private static String detectType(String tempContents) {
        String heuristics = "";
        if ((tempContents.contains("youtube.com"))) {
            heuristics = "youtube";
        }
        if ((tempContents.contains("password"))) {
            heuristics = "Passwords";
        }
        if ((tempContents.contains("<?php") || (tempContents.contains("include")))) {
            heuristics = "PHP_C++";
        }

        if ((tempContents.matches("using.[;]"))) {
            //The regex here means "using" at the beginning and ";" at the end (in a given phrase).
            heuristics = "C++";
        }
        if ((tempContents.contains("public class") || (tempContents.contains("public static void")))) {
            heuristics = "Java";
        }
        if ((tempContents.contains("elif") || (tempContents.contains("endif")))) {
            heuristics = "BashScripts";
        }
        if ((tempContents.contains("<html>") || (tempContents.contains("<body>")) || (tempContents.contains("<div>")))) {
            heuristics = "HTML";
        }
        if ((tempContents.contains("<?xml version"))) {
            heuristics = "XML";
        }
        if ((tempContents.contains("DEBUG"))) {
            heuristics = "Debug_Logs";
        } else {
            heuristics = "";
        }
        return heuristics;

    }

    private static void startDownloadThread() {
        //Code for Download Queueing.

        for (int i = 0; i < genericPastes.size(); i++) {
            pool.addPaste(genericPastes.get(i));
            pool.addRecord(genericPastes.get(i)); //adds record for future comparison to prevent duplicate entries into thread jobs
        }
//        for (Paste p : genericPastes) {
//            pool.addPaste(p);
//            pool.addRecord(p); //adds record for future comparison to prevent duplicate entries into thread jobs
//        }
//        diffGenericPastes.clear();

        PasteThread pThread = new PasteThread();
        pThread.init(pool, blockedLock, cdlatch, 30);
        Thread t1 = new Thread(pThread);
        threadArr.add(t1);
        t1.start();
    }

    private static void addToPool() {
        for (Paste p : genericPastes) {
            pool.addPaste(p);
            pool.addRecord(p);
        }
    }

    private static void resumeThread() {
        synchronized (blockedLock) {
            cdlatch.countDown();
        }
    }
}