package com.company;

import java.util.LinkedList;

public class URLPool {

    private LinkedList<URLDepthPair> Processed = new LinkedList<URLDepthPair>();
    private LinkedList<URLDepthPair> NotProcessed = new LinkedList<URLDepthPair>();
    private int Depth;
    private int Waiting;
    private int Threads;

    public URLPool(String url, int depth, int threads) {
        NotProcessed.add(new URLDepthPair(url, depth));
        Depth = depth;
        Threads = threads;
    }

    public synchronized URLDepthPair get() throws InterruptedException {
        if (isEmpty()) {
            Waiting++;
            if (Waiting == Threads) {
                getSites();
                System.exit(0);
            }
            wait();
        }
        return NotProcessed.removeFirst();
    }
    public synchronized void addNotProcessed(URLDepthPair pair) {
        NotProcessed.add(pair);
        if (Waiting > 0) {
            Waiting--;
            notify();
        }
    }

    private boolean isEmpty() {
        if (NotProcessed.size() == 0) return true;
        return false;
    }

    public void getSites() {
        System.out.println("Depth: " + Depth);
        for (int i = 0; i < Processed.size(); i++) {
            System.out.println( Depth - Processed.get(i).getDepth() + " " +  Processed.get(i).getURL());
        }
        System.out.println("Links visited: " + Processed.size());
    }


    public void addProcessed(URLDepthPair pair) {
        Processed.add(pair);
    }

    public LinkedList<URLDepthPair> getProcessed()
    {
        return Processed;
    }

    public LinkedList<URLDepthPair> getNotProcessed()
    {
        return NotProcessed;
    }

}
