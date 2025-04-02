package pt.ulisboa.tecnico.tuplespaces.replicaserver.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

import java.util.Map;
import java.util.HashMap;


public class ServerState {

    boolean DEBUG;
    private List<String> tuples;                            // tuple space
    
    private Map<String, Integer> locks;                     // mapped lock for the TAKE operation
                                                            // locks={"<a,b>": -1, "<c,d>": 1, "<e,f>": -1, "<g,h>": 2, ...}
                                                            // key: tuple, value: N (lock status)
                                                            // N = -1 => the tuple is free
                                                            // N > 0  => the tuple is locked by client N
    
    
    private boolean lock = false;                           // server lock state for the TAKE operation
    private int lockHolder = -1;                            // client ID holding the lock
    private Queue<Integer> lockQueue;  // queue for pending lock requests


    public ServerState(boolean debug) {
        this.DEBUG = debug;
        this.tuples = new ArrayList<String>();
        this.lockQueue = new LinkedList<Integer>();

        this.locks = new HashMap<String, Integer>();
    }

    /**
     * REQUEST-LOCK operation:  tries to acquire the lock(s) for a client
     *
     * @param clientId the client ID
     * @param pattern the pattern to match or the tuple to match
     * @return true if the lock is granted to the client
     */
    public List<String> acquireLock(int clientId, String pattern) {

        while (true) {
            synchronized (this) {  // Only synchronize the critical section
                
                // TODO: what if we verify if the pattern is a regular expression?
                // if not, we return the list with the first tuple that matches the pattern
                // if the pattern is a regular expression, we return the list with all tuples that match the pattern

                List<String> matches = new ArrayList<String>(); // list of tuples that match the pattern
                
                for (Map.Entry<String, Integer> entry : this.locks.entrySet()) {    // iterate over the tuple space
                    if (entry.getKey().matches(pattern)) {  // if the tuple matches the pattern
                        if (entry.getValue() == -1) {       // if the tuple is free
                            entry.setValue(clientId);      // lock the tuple for the client
                            matches.add(entry.getKey());   // add the tuple to the list of matches
                            if (DEBUG) {
                                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Lock granted to client %d for tuple %s\n", clientId, entry.getKey());
                            }
                        }
                        else {
                            // if the tuple matches the pattern but is locked by another client
                            // Xu Liskov algorithm: return unsuccessful (empty list) to force the client to retry
                            if (DEBUG) {
                                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Lock denied to client %d for tuple %s - client needs to retry\n", clientId, entry.getKey());
                            }
                            return new ArrayList<String>(); // return empty list
                        }
                    }
                }

                if (matches.isEmpty()) { // MISS: the client didn't get any locks, i.e., there are no matching tuples
                    if (DEBUG) {
                        System.err.printf("[\u001B[34mDEBUG\u001B[0m] No tuples found for client %d with pattern %s. Block until a tuple is added\n", clientId, pattern);
                    }
                    try {
                        wait(); // block until a tuple is added
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                else { // HIT: the client got successfully locks for the tuples
                    if (DEBUG) {
                        // for debugging purposes, print the hashmap of locks
                        System.err.println("[\u001B[34mDEBUG\u001B[0m] Locks: " + this.locks);
                    }
                    return matches; // return the list of tuples that match the pattern
                }
            }
        }
    }

    /**
     * REQUEST-UNLOCK operation:    releases the lock(s) for a client
     */
    public synchronized void freeLock(int clientId) {
        
        if (DEBUG) {
            // for debugging purposes, print the hashmap of locks and the client ID
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Freeing lock for client %d\n", clientId);
            System.err.println("[\u001B[34mDEBUG\u001B[0m] Locks: " + this.locks);
        }

        for (Map.Entry<String, Integer> entry : this.locks.entrySet()) {    // iterate over the tuple space
            if (entry.getValue() == clientId) {       // if the tuple is locked by the client
                entry.setValue(-1);                   // unlock the tuple for the client
                if (DEBUG) {
                    System.err.printf("[\u001B[34mDEBUG\u001B[0m] Lock released for client %d for tuple %s\n", clientId, entry.getKey());
                }
            }
        }
    }

    /**
     * PUT operation:   adds a tuple to the tuple space
     * @param tuple the tuple to be added
     */
    public synchronized void put(String tuple) {
        this.tuples.add(tuple);
        this.locks.put(tuple, -1);

        if (DEBUG) {
            System.err.println("[\u001B[34mDEBUG\u001B[0m] Added tuple: " + tuple);
        }

        notifyAll();
    }

    /**
     * returns a tuple that matches the pattern that may be a regular expression
     * @param pattern the pattern to match
     * @return the tuple that matches the pattern
     */
    private String getMatchingTuple(String pattern) {
        for (String tuple : this.tuples) {
            if (tuple.matches(pattern)) {
                return tuple;
            }
        }
        return null;
    }

    /**
     * READ operation:  accepts a tuple description to find a match in the tuple space
     *                  blocks the client until there is a tuple that satisfies description
     *                  the tuple is not removed from the tuple space
     *
     * @param pattern the pattern to match
     * @return the tuple that matches the pattern
     */
    public synchronized String read(String pattern) {
        while (true) {
            String t = getMatchingTuple(pattern);
            if (t != null) {
                if (DEBUG) {
                    System.err.printf("[\u001B[34mDEBUG\u001B[0m] Read tuple %s for pattern: %s%n", t, pattern);
                }
                return t;
            }

            if (DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Blocking %s because no tuple found for pattern: %s\n\n", Thread.currentThread().getName(), pattern);
            }

            try {
                wait(); // waits until a tuple is available
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * TAKE operation:  accepts a tuple description to find a match in the tuple space
     *                  blocks the client until there is a tuple that satisfies description
     *                  the tuple is removed from the tuple space
     *
     * @param pattern the pattern to match
     * @return the tuple that matches the pattern
     */
    public synchronized String take(String pattern) {
        while (true) {
            String t = getMatchingTuple(pattern);
            if (t != null) {
                this.tuples.remove(t);
                this.locks.remove(t);

                if (DEBUG) {
                    System.err.printf("[\u001B[34mDEBUG\u001B[0m] Took tuple %s for pattern: %s%n", t, pattern);
                }

                return t;
            }

            if (DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Blocking %s because no tuple found for pattern: %s\n\n", Thread.currentThread().getName(), pattern);
            }

            try {
                wait(); // waits until a tuple is available
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * GET-TUPLE-SPACES-STATE operation: returns the tuple space state of the server
     * @return the tuple space state of the server
     */
    public synchronized List<String> getTupleSpacesState() {

        List<String> tupleSpacesState = new ArrayList<String>(this.tuples);

        if (DEBUG) {
            System.err.println("[\u001B[34mDEBUG\u001B[0m] Got tuple space state");
        }

        return tupleSpacesState;
    }
}
