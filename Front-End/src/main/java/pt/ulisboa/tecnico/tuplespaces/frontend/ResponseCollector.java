package pt.ulisboa.tecnico.tuplespaces.frontend;

import java.util.ArrayList;


/**
 * the ResponseCollector class is responsible for collecting the responses from the TupleSpaces servers
 * it collects the responses from the server(s) and provides a method to retrieve the collected responses
 */
public class ResponseCollector {

    private ArrayList<ResponseEntry> collectedHistory;


    public ResponseCollector() {
        this.collectedHistory = new ArrayList<ResponseEntry>();
    }


    public synchronized void addResponse(String requestType, int requestId, String request, String response, int serverId) {
        this.collectedHistory.add(new ResponseEntry(requestType, requestId, request, response, serverId));
        notifyAll();
    }

    public synchronized String getResponse(int requestId, String requestType) {
        if (requestType.equals("PUT")) {
            int requestsCounter = 0;
            int okCounter = 0;

            for (ResponseEntry e : this.collectedHistory) {
                if (e.getRequestId() == requestId) {
                    System.err.println("[\u001B[34mDEBUG\u001B[0m] entry = " + e.toString());
                    requestsCounter++;
                    if (e.getResponse().equals("OK")) { okCounter++; }
                    if (requestsCounter == 3) { break; }
                }
            }
            if (okCounter == 3) { return "OK"; }
            else { return "NO"; }
        }
        else if (requestType.equals("READ")) {  // TODO
            for (ResponseEntry e : this.collectedHistory) {
                if (e.getRequestId() == requestId) {
                    return e.getResponse();
                }
            }
        }
        return "NO";
    }

    /**
     * this method is used to wait until all the responses for a given request ID are received.
     * @param requestId the request ID to wait for
     * @param requestType the type of request
     */
    public synchronized void waitUntilAllReceived(int requestId, String requestType) {
        int requestsCounter;

        while (true) {
            requestsCounter = 0;

            if (requestType.equals("PUT")) {
                for (ResponseEntry e : this.collectedHistory) {
                    if (e.getRequestId() == requestId) {
                        requestsCounter++;
                        if (requestsCounter == 3) { return; }
                    }
                }
                
                try { wait(); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
            else if (requestType.equals("READ")) {
                for (ResponseEntry e : this.collectedHistory) {
                    if (e.getRequestId() == requestId) {
                        return;
                    }
                }

                try { wait(); }
                catch (InterruptedException e) { e.printStackTrace(); }
            } 
        }
    }
}











