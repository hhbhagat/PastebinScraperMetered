package pastebin;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PasteThread implements Runnable {

    private PastePool pool = null;
    private ArrayList<Paste> pasteArr = new ArrayList<Paste>();
    private Lock blockedLock = null;
    private int chunkSize = 0;
    CountDownLatch latch = null;

    @Override
    public void run() {

        synchronized (blockedLock) {

            System.out.println("Started proc.");
            do {
                do {
                    for (int i = 0; i < 10; i++) {
                        getMorePastes();
                        PasteDownloader.savePastes(pasteArr);
                        if (!pasteArr.isEmpty()) {
                            System.out.println("Waiting to prevent lockout...");
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException ex) {
                            }
                        } else {
                            System.out.println("Empty run.");
                        }
                    }

                    pasteArr = new ArrayList<Paste>(); //reset list after for loop

                } while (!pasteArr.isEmpty());

                System.out.println("Waiting for wakeup..");
                try {
                    latch.await();
                } catch (InterruptedException ex) {
                }
                System.out.println("Woken up.");
                latch = new CountDownLatch(1); //reset countdown latch
                pasteArr = new ArrayList<Paste>();
                pasteArr.add(new VolatilePaste()); //add dummy paste to restart inner while

                System.out.println("Finished a set.");
            } while (true);
        }
    }

    public void init(PastePool pp, Lock l, CountDownLatch la, int cs) {
        pool = pp;
        blockedLock = l;
        chunkSize = cs;
        latch = la;
    }

    public void getMorePastes() {
        pasteArr = new ArrayList<Paste>();
        pool.printSize();
        for (int i = 0; i <= chunkSize; i++) {
            Paste p = pool.getPaste();
            if (p != null) {
                pasteArr.add(p);
            }
        }
    }
}
