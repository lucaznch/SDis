package pt.ulisboa.tecnico.tuplespaces.frontend;

// classes generated from the proto file
import pt.ulisboa.tecnico.tuplespaces.replicated.contract.TupleSpacesGrpc;
import pt.ulisboa.tecnico.tuplespaces.replicated.contract.TupleSpacesOuterClass;

import io.grpc.stub.StreamObserver;     // StreamObserver is used to send responses to the Client

import io.grpc.ManagedChannel;          // ManagedChannel is used to create a channel to the Server
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.List;


/**
 * FrontendImpl is the class that acts as the intermediary between the clients and the servers.
 * It extends the TupleSpacesGrpc.TupleSpacesImplBase class that was generated from the proto file.
 * It overrides the methods defined in the proto file.
 * It receives the requests from the clients and forwards them to the servers.
 */
public class FrontendImpl extends TupleSpacesGrpc.TupleSpacesImplBase {

    private boolean DEBUG;
    private final int numServers;                           // number of servers
    private final ResponseCollector collector;              // frontend(client): collector is responsible for collecting the responses from the TupleSpaces servers
    private final ManagedChannel[] channels;                // frontend(client): channels is the abstraction to connect to the server endpoints
    private final TupleSpacesGrpc.TupleSpacesStub[] stubs;  // frontend(client): stubs are used to make remote calls to the server. 
                                                            // frontend(client) will use non-blocking stubs to make remote calls to the server

    public FrontendImpl(boolean debug, int numServers, String[] servers) {
        this.DEBUG = debug;
        this.numServers = numServers;
        this.collector = new ResponseCollector();
        this.channels = new ManagedChannel[numServers];
        this.stubs = new TupleSpacesGrpc.TupleSpacesStub[numServers];

        for (int i = 0; i < numServers; i++) {
            this.channels[i] = ManagedChannelBuilder.forTarget(servers[i])
                                                    .usePlaintext()
                                                    .build();       // create a channel to each server
            this.stubs[i] = TupleSpacesGrpc.newStub(channels[i]);   // create non-blocking stub to make remote calls to each server
        }
    }


    /**
     * this method is called when a PUT request is received from the client
     * it forwards the request to the server and sends the response back to the client
     * 
     * @param clientRequest the request received from the client
     * @param clientResponseObserver the observer to send the response back to the client
     */
    @Override
    public void put(TupleSpacesOuterClass.PutRequest clientRequest, StreamObserver<TupleSpacesOuterClass.PutResponse> clientResponseObserver) {
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received PUT request from client in %s, %s", Thread.currentThread().getName(), clientRequest);
        }

        String tuple = clientRequest.getNewTuple();             // get the tuple from the request sent by the CLIENT

        TupleSpacesOuterClass.PutRequest serverRequest = 
                                TupleSpacesOuterClass.PutRequest
                                                    .newBuilder()
                                                    .setNewTuple(tuple)
                                                    .build();   // construct a new Protobuffer object to send as request to the SERVER

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending PUT request to servers... tuple: %s\n", tuple);
        }

        try {
            // make async calls sending the request to every server
            for (int i = 0; i < this.numServers; i++) {
                this.stubs[i].put(serverRequest, new FrontendPutObserver(this.collector));
                if (this.DEBUG) {
                    System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sent PUT request to server %d\n", i);
                }
            }

            this.collector.waitUntilAllReceived(numServers); // wait until all servers respond

            if (this.DEBUG) {
                System.err.println("[\u001B[34mDEBUG\u001B[0m] Frontend received PUT responses from all servers");
            }

            String result = this.collector.getResponse(this.numServers);

            TupleSpacesOuterClass.PutResponse clientResponse =
                                TupleSpacesOuterClass.PutResponse
                                                    .newBuilder()
                                                    .setOk(result)
                                                    .build();   // construct a new Protobuffer object to send as response to the CLIENT

            clientResponseObserver.onNext(clientResponse);      // use the responseObserver to send the response
            clientResponseObserver.onCompleted();               // after sending the response, complete the call
        }
        catch (StatusRuntimeException e) {
            if (this.DEBUG) {
                System.err.println("[\u001B[34mDEBUG\u001B[0m] Frontend PUT request \u001B[31merror\u001B[0m: " + e.getMessage());
            }
        }
    }

    /**
     * this method is called when a READ request is received from the client
     * it forwards the request to the server and sends the response back to the client
     * 
     * @param clientRequest the request received from the client
     * @param clientResponseObserver the observer to send the response back to the client
     */
    /*
    @Override
    public void read(TupleSpacesOuterClass.ReadRequest clientRequest, StreamObserver<TupleSpacesOuterClass.ReadResponse> clientResponseObserver) {
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received READ request from client in %s, %s", Thread.currentThread().getName(), clientRequest);
        }

        String searchPattern = clientRequest.getSearchPattern();// get the search pattern from the request sent by the CLIENT

        // create the request to send to the SERVER
        TupleSpacesOuterClass.ReadRequest serverRequest =
                                TupleSpacesOuterClass.ReadRequest
                                                    .newBuilder()
                                                    .setSearchPattern(searchPattern)
                                                    .build();   // construct a new Protobuffer object to send as request to the SERVER

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending READ request to server... search pattern: %s\n", searchPattern);
        }

        try {
            TupleSpacesOuterClass.ReadResponse serverResponse = 
                                this.stub.read(serverRequest);  // send the request to the SERVER and get the response from the SERVER

            String tuple = serverResponse.getResult();          // get the tuple from the response object sent by the SERVER

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received READ response from server, tuple: %s\n", tuple);
            }

            TupleSpacesOuterClass.ReadResponse clientResponse = 
                                TupleSpacesOuterClass.ReadResponse
                                                    .newBuilder()
                                                    .setResult(tuple)
                                                    .build();   // construct a new Protobuffer object to send as response to the CLIENT

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending READ response back to client in %s\n\n", Thread.currentThread().getName());
            }

            clientResponseObserver.onNext(clientResponse);      // use the responseObserver to send the response
            clientResponseObserver.onCompleted();               // after sending the response, complete the call
        }
        catch (StatusRuntimeException e) {
            if (this.DEBUG) {
                System.err.println("[\u001B[34mDEBUG\u001B[0m] Frontend READ request \u001B[31merror\u001B[0m: " + e.getMessage());
            }
        }
    }
    */

    /**
     * this method is called when a TAKE request is received from the client
     * it forwards the request to the server and sends the response back to the client
     * 
     * @param clientRequest the request received from the client
     * @param clientResponseObserver the observer to send the response back to the client
     */
    /*
    @Override
    public void take(TupleSpacesOuterClass.TakeRequest clientRequest, StreamObserver<TupleSpacesOuterClass.TakeResponse> clientResponseObserver) {
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received TAKE request from client in %s, %s", Thread.currentThread().getName(), clientRequest);
        }

        String searchPattern = clientRequest.getSearchPattern();// get the search pattern from the request sent by the CLIENT

        TupleSpacesOuterClass.TakeRequest serverRequest =
                                TupleSpacesOuterClass.TakeRequest
                                                    .newBuilder()
                                                    .setSearchPattern(searchPattern)
                                                    .build();   // construct a new Protobuffer object to send as request to the SERVER

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending TAKE request to server... search pattern: %s\n", searchPattern);
        }

        try {
            TupleSpacesOuterClass.TakeResponse serverResponse =
                                this.stub.take(serverRequest);  // send the request to the SERVER and get the response from the SERVER

            String tuple = serverResponse.getResult();          // get the tuple from the response object sent by the SERVER

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received TAKE response from server, tuple: %s\n", tuple);
            }

            TupleSpacesOuterClass.TakeResponse clientResponse = 
                                TupleSpacesOuterClass.TakeResponse
                                                    .newBuilder()
                                                    .setResult(tuple)
                                                    .build();   // construct a new Protobuffer object to send as response to the CLIENT

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending TAKE response back to client in %s\n\n", Thread.currentThread().getName());
            }

            clientResponseObserver.onNext(clientResponse);      // use the responseObserver to send the response
            clientResponseObserver.onCompleted();               // after sending the response, complete the call
        }
        catch (StatusRuntimeException e) {
            if (this.DEBUG) {
                System.err.println("[\u001B[34mDEBUG\u001B[0m] Frontend TAKE request \u001B[31merror\u001B[0m: " + e.getMessage());
            }
        }
    }
    */

    /**
     * this method is called when a GET-TUPLE-SPACES-STATE request is received from the client
     * it forwards the request to the server and sends the response back to the client
     * 
     * @param clientRequest the request received from the client
     * @param clientResponseObserver the observer to send the response back to the client
     */
    /*
    @Override
    public void getTupleSpacesState(TupleSpacesOuterClass.getTupleSpacesStateRequest clientRequest, StreamObserver<TupleSpacesOuterClass.getTupleSpacesStateResponse> clientResponseObserver) {
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received GET-TUPLE-SPACES-STATE request from client in %s%n", Thread.currentThread().getName());
        }

        TupleSpacesOuterClass.getTupleSpacesStateRequest serverRequest = 
                                TupleSpacesOuterClass.getTupleSpacesStateRequest
                                                    .newBuilder()
                                                    .build();   // construct a new Protobuffer object to send as request to the SERVER

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending GET-TUPLE-SPACES-STATE request to server\n");
        }

        try {
            TupleSpacesOuterClass.getTupleSpacesStateResponse serverResponse = 
                                this.stub.getTupleSpacesState(serverRequest);   // send the request to the SERVER and get the response from the SERVER

            // get the state from the response object sent by the SERVER by iterating over the list of tuples
            int i = 0;
            int tupleSpacesStateCount = serverResponse.getTupleCount();         // length of the list of tuples
            List<String> state = new ArrayList<>();

            while (i < tupleSpacesStateCount) {
                state.add(serverResponse.getTuple(i));                          // iterate over the list of tuples and add them to the state
                i++;
            }

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received GET-TUPLE-SPACES-STATE response from server, state: %s\n", state.toString());
            }

            // create the response to send to the CLIENT
            TupleSpacesOuterClass.getTupleSpacesStateResponse.Builder clientResponseBuilder =
                                TupleSpacesOuterClass.getTupleSpacesStateResponse.newBuilder();  // create a response builder object to build the response

            for (String tuple : state) {
                clientResponseBuilder.addTuple(tuple);                          // keep adding tuples to response
            }

            TupleSpacesOuterClass.getTupleSpacesStateResponse clientResponse = clientResponseBuilder.build();   // build the response

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending GET-TUPLE-SPACES-STATE response back to client in %s\n\n", Thread.currentThread().getName());
            }

            clientResponseObserver.onNext(clientResponse);                      // use the responseObserver to send the response
            clientResponseObserver.onCompleted();                               // after sending the response, complete the call
        }
        catch (StatusRuntimeException e) {
            if (this.DEBUG) {
                System.err.println("[\u001B[34mDEBUG\u001B[0m] Frontend GET-TUPLE-SPACES-STATE request \u001B[31merror\u001B[0m: " + e.getMessage());
            }
        }
    }
    */
}




