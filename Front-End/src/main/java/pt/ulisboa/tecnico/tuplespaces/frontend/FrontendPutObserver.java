package pt.ulisboa.tecnico.tuplespaces.frontend;

// classes generated from the proto file
import pt.ulisboa.tecnico.tuplespaces.replicated.contract.TupleSpacesGrpc;
import pt.ulisboa.tecnico.tuplespaces.replicated.contract.TupleSpacesOuterClass;

import io.grpc.stub.StreamObserver;



/**
 * The FrontendObserver class is responsible for handling the responses from the TupleSpaces servers.
 * It handles the asynchronous responses from the servers and forwards them to the ResponseCollector.
 * 
 */
public class FrontendPutObserver implements StreamObserver<TupleSpacesOuterClass.PutResponse> {

    private ResponseCollector collector;

    public FrontendPutObserver(ResponseCollector c) {
        this.collector = c;
    }

    /**
     * This method is called when a PUT response is received from the server.
     * It forwards the response to the ResponseCollector.
     * 
     * @param response the response received from the server
     */
    @Override
    public void onNext(TupleSpacesOuterClass.PutResponse response) {        
        collector.addResponse(response.getOk());
        System.err.printf("[\u001B[34mDEBUG\u001B[0m] FrontendPutObserver: received PUT response from a server: %s\n", response.getOk());
    }

    /**
     * This method is called when an error occurs in the communication with the server.
     * 
     * @param t the error that occurred
     */
    @Override
    public void onError(Throwable t) {
        System.out.println("PUT error: " + t.getMessage());
    }

    /**
     * This method is called when the communication with the server is completed.
     */
    @Override
    public void onCompleted() {
        System.out.println("[\u001B[34mDEBUG\u001B[0m] FrontendPutObserver: PUT completed");
    }
}






