package pt.ulisboa.tecnico.tuplespaces.frontend;

import java.util.ArrayList;


/**
 * The ResponseCollector class is responsible for collecting the responses from the TupleSpaces server(s).
 * It collects the responses from the server(s) and provides a method to retrieve the collected responses.
 */
public class ResponseCollector {

    private ArrayList<String> collectedResponses;


    public ResponseCollector() {
        this.collectedResponses = new ArrayList<String>();
    }


    public synchronized void addResponse(String response) {
        this.collectedResponses.add(response);
        notifyAll();
    }

    /**
     * This method returns the last n responses received.
     * 
     * @param n the number of responses to return
     * @return the intersection of the last n responses received
     */
    public synchronized String getResponse(int n) {
        int len = this.collectedResponses.size();

        if (n == 3) {
            if (len < 3) { return "NO"; }

            if (this.collectedResponses.get(len - 3).equals("OK") &&
                this.collectedResponses.get(len - 2).equals("OK") &&
                this.collectedResponses.get(len - 1).equals("OK")) {
                return "OK";
            } else {
                return "NO";
            }
        }
        else if (n == 1) {
            if (len == 0) { return "NO"; }
            else {
                return this.collectedResponses.get(len - 1);
            }
        }
        return "NO";
    }

    /**
     * This method waits until a certain number of responses are received.
     * 
     * @param n the number of responses to wait for
     * @throws InterruptedException
     */
    public synchronized void waitUntilAllReceived(int n) {
        if (n == 3) {       // wait for 3 responses
            do {
                try {
                    wait();     // block until notified of a new response
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (this.collectedResponses.size() % 3 != 0);  // check if we got three responses
        }

        else if (n == 1) {  // wait for 1 response
            int size = this.collectedResponses.size();
            do {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (this.collectedResponses.size() == size + 1);
        }
    }
}











