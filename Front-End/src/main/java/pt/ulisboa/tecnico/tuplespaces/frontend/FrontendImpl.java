package pt.ulisboa.tecnico.tuplespaces.frontend;

// classes generated from the proto file
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.TupleSpacesGrpc;
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.TupleSpacesOuterClass;

import io.grpc.stub.StreamObserver;     // StreamObserver is used to send responses to the client

import io.grpc.ManagedChannel;          // ManagedChannel is used to create a channel to the server
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.List;


public class FrontendImpl extends TupleSpacesGrpc.TupleSpacesImplBase {

    private boolean DEBUG;
    private String host_port;
    private final ManagedChannel channel;                           // channel to the server
    private final TupleSpacesGrpc.TupleSpacesBlockingStub stub;     // stub to call the server

    public FrontendImpl(boolean debug, String host_port) {
        this.DEBUG = debug;
        this.host_port = host_port;
        this.channel = ManagedChannelBuilder.forTarget(host_port).usePlaintext().build();
        this.stub = TupleSpacesGrpc.newBlockingStub(channel);
    }


    @Override
    public void put(TupleSpacesOuterClass.PutRequest clientRequest, StreamObserver<TupleSpacesOuterClass.PutResponse> clientResponseObserver) {

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received PUT request from client in %s, %s", Thread.currentThread().getName(), clientRequest);
        }

        // get the tuple sent by the CLIENT
        String tuple = clientRequest.getNewTuple();

        // create the request to send to the SERVER
        TupleSpacesOuterClass.PutRequest serverRequest = TupleSpacesOuterClass.PutRequest.newBuilder().setNewTuple(tuple).build();

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending PUT request to server... tuple: %s\n", tuple);
        }

        try {
            // send the request to the SERVER and get the response from the SERVER
            TupleSpacesOuterClass.PutResponse serverResponse = this.stub.put(serverRequest);

            // in this specific case, the response from the SERVER is nothing
            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received PUT response from server%n");
            }

            // create the response to send to the CLIENT
            TupleSpacesOuterClass.PutResponse clientResponse = TupleSpacesOuterClass.PutResponse.newBuilder().build();

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending PUT response back to client in %s\n\n", Thread.currentThread().getName());
            }

            // send the response to the CLIENT
            clientResponseObserver.onNext(clientResponse);
            clientResponseObserver.onCompleted();
        }
        catch (StatusRuntimeException e) {
            if (this.DEBUG) {
                System.err.println("[\u001B[34mDEBUG\u001B[0m] Frontend PUT request u001B[31merror\u001B[0m: " + e.getMessage());
            }
        }
    }

    @Override
    public void read(TupleSpacesOuterClass.ReadRequest clientRequest, StreamObserver<TupleSpacesOuterClass.ReadResponse> clientResponseObserver) {
    
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received READ request from client in %s, %s", Thread.currentThread().getName(), clientRequest);
        }

        // get the search pattern sent by the CLIENT
        String searchPattern = clientRequest.getSearchPattern();

        // create the request to send to the SERVER
        TupleSpacesOuterClass.ReadRequest serverRequest = TupleSpacesOuterClass.ReadRequest.newBuilder().setSearchPattern(searchPattern).build();

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending READ request to server... search pattern: %s\n", searchPattern);
        }

        try {
            // send the request to the SERVER and get the response from the SERVER
            TupleSpacesOuterClass.ReadResponse serverResponse = this.stub.read(serverRequest);

            // get the tuple from the response
            String tuple = serverResponse.getResult();

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received READ response from server, tuple: %s\n", tuple);
            }

            // create the response to send to the CLIENT
            TupleSpacesOuterClass.ReadResponse clientResponse = TupleSpacesOuterClass.ReadResponse.newBuilder().setResult(tuple).build();

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending READ response back to client in %s\n\n", Thread.currentThread().getName());
            }

            // send the response to the CLIENT
            clientResponseObserver.onNext(clientResponse);
            clientResponseObserver.onCompleted();
        }
        catch (StatusRuntimeException e) {
            if (this.DEBUG) {
                System.err.println("[\u001B[34mDEBUG\u001B[0m] Frontend READ request u001B[31merror\u001B[0m: " + e.getMessage());
            }
        }
    }

    @Override
    public void take(TupleSpacesOuterClass.TakeRequest clientRequest, StreamObserver<TupleSpacesOuterClass.TakeResponse> clientResponseObserver) {
    
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received TAKE request from client in %s, %s", Thread.currentThread().getName(), clientRequest);
        }

        // get the search pattern sent by the CLIENT
        String searchPattern = clientRequest.getSearchPattern();

        // create the request to send to the SERVER
        TupleSpacesOuterClass.TakeRequest serverRequest = TupleSpacesOuterClass.TakeRequest.newBuilder().setSearchPattern(searchPattern).build();

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending TAKE request to server... search pattern: %s\n", searchPattern);
        }

        try {
            // send the request to the SERVER and get the response from the SERVER
            TupleSpacesOuterClass.TakeResponse serverResponse = this.stub.take(serverRequest);

            // get the tuple from the response
            String tuple = serverResponse.getResult();

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received TAKE response from server, tuple: %s\n", tuple);
            }

            // create the response to send to the CLIENT
            TupleSpacesOuterClass.TakeResponse clientResponse = TupleSpacesOuterClass.TakeResponse.newBuilder().setResult(tuple).build();

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending TAKE response back to client in %s\n\n", Thread.currentThread().getName());
            }

            // send the response to the CLIENT
            clientResponseObserver.onNext(clientResponse);
            clientResponseObserver.onCompleted();
        }
        catch (StatusRuntimeException e) {
            if (this.DEBUG) {
                System.err.println("[\u001B[34mDEBUG\u001B[0m] Frontend TAKE request u001B[31merror\u001B[0m: " + e.getMessage());
            }
        }
    }

    @Override
    public void getTupleSpacesState(TupleSpacesOuterClass.getTupleSpacesStateRequest clientRequest, StreamObserver<TupleSpacesOuterClass.getTupleSpacesStateResponse> clientResponseObserver) {
        
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received GET-TUPLE-SPACES-STATE request from client in %s%n", Thread.currentThread().getName());
        }

        // create the request to send to the SERVER
        TupleSpacesOuterClass.getTupleSpacesStateRequest serverRequest = TupleSpacesOuterClass.getTupleSpacesStateRequest.newBuilder().build();

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending GET-TUPLE-SPACES-STATE request to server\n");
        }

        try {
            // send the request to the SERVER and get the response from the SERVER
            TupleSpacesOuterClass.getTupleSpacesStateResponse serverResponse = this.stub.getTupleSpacesState(serverRequest);

            // get the state from the response
            int i = 0;
            int tupleSpacesStateCount = serverResponse.getTupleCount();
            List<String> state = new ArrayList<>();

            while (i < tupleSpacesStateCount) {
                state.add(serverResponse.getTuple(i));
                i++;
            }

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend received GET-TUPLE-SPACES-STATE response from server, state: %s\n", state.toString());
            }

            // create the response to send to the CLIENT
            TupleSpacesOuterClass.getTupleSpacesStateResponse.Builder clientResponseBuilder = TupleSpacesOuterClass.getTupleSpacesStateResponse.newBuilder();
            for (String tuple : state) {
                clientResponseBuilder.addTuple(tuple);
            }
            TupleSpacesOuterClass.getTupleSpacesStateResponse clientResponse = clientResponseBuilder.build();

            if (this.DEBUG) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend sending GET-TUPLE-SPACES-STATE response back to client in %s\n\n", Thread.currentThread().getName());
            }

            // send the response to the CLIENT
            clientResponseObserver.onNext(clientResponse);
            clientResponseObserver.onCompleted();
        }
        catch (StatusRuntimeException e) {
            if (this.DEBUG) {
                System.err.println("[\u001B[34mDEBUG\u001B[0m] Frontend GET-TUPLE-SPACES-STATE request u001B[31merror\u001B[0m: " + e.getMessage());
            }
        }
    }
}




