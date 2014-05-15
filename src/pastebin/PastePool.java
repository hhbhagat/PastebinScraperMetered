package pastebin;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PastePool {

    private Deque<Paste> pasteQueue = new LinkedList<Paste>();
    private Lock blockedLock = new ReentrantLock();
    private ArrayList<Paste> addedPastes = new ArrayList<Paste>();

    public void addPaste(Paste p) {
        pasteQueue.offerFirst(p);
    }
    
    public void addRecord(Paste p) {
        addedPastes.add(p);
    }
    
    public ArrayList<Paste> getAllRecords(){
        return addedPastes;
    }

    public synchronized Paste getPaste() {
        
        try {
            return pasteQueue.pollLast();
        } catch (Exception e) {
            System.out.println("Queue is empty");
            return null;
        }
    }
    
    public synchronized void printSize(){
        System.out.println("Size is " + pasteQueue.size());
    }
    
}
