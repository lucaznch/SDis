package pt.ulisboa.tecnico.tuplespaces.server.domain;

import java.util.ArrayList;
import java.util.List;

public class ServerState {

    private List<String> tuples;    // tuple space

    public ServerState() {
        this.tuples = new ArrayList<String>();
    }


    /**
     * PUT operation:   adds a tuple to the tuple space
     * @param tuple the tuple to be added
     */
    public synchronized void put(String tuple) {        // SHOULD IT BE SYNCHRONIZED ???
        this.tuples.add(tuple);
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
     * READ operation:  accepts a tuple description and returns a tuple that matches the description
     *                  blocks the client until there is a tuple that satisfies the description
     *                  the tuple is not removed from the tuple space
     *
     * @param pattern the pattern to match
     * @return the tuple that matches the pattern
     */
    public synchronized String read(String pattern) {
        while (true) {
            String t = getMatchingTuple(pattern);
            if (t != null) {
                return t;
            }

            try {
                wait(); // waits until a tuple is available
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * TAKE operation:  accepts a tuple description and returns a tuple that matches the description
     *                  blocks the client until there is a tuple that satisfies the description
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
                return t;
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
        return new ArrayList<String>(this.tuples);
    }
}
