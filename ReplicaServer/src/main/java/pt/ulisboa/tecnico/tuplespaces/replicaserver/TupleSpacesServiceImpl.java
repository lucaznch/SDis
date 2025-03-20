package pt.ulisboa.tecnico.tuplespaces.replicaserver;

// classes generated from the proto file
import pt.ulisboa.tecnico.tuplespaces.replicated.contract.TupleSpacesGrpc;
import pt.ulisboa.tecnico.tuplespaces.replicated.contract.TupleSpacesOuterClass;

import pt.ulisboa.tecnico.tuplespaces.replicaserver.domain.ServerState;

import java.util.List;
import java.util.Random;

import io.grpc.stub.StreamObserver;


/**
 * TupleSpacesServiceImpl is the class that implements the gRPC service.
 * It extends the TupleSpacesGrpc.TupleSpacesImplBase class that was generated from the proto file.
 * It overrides the methods defined in the proto file.
 * It receives the requests from the clients and processes them.
 * It uses the ServerState class to store the tuple space and perform the operations.
 */
public class TupleSpacesServiceImpl extends TupleSpacesGrpc.TupleSpacesImplBase {

    private boolean DEBUG;
    private ServerState serverState;

    public TupleSpacesServiceImpl(boolean debug) {
        this.DEBUG = debug;
        this.serverState = new ServerState(debug); 
    }

    @Override
    public void put(TupleSpacesOuterClass.PutRequest request, StreamObserver<TupleSpacesOuterClass.PutResponse> responseObserver) {
        final int maxTimeout = 5000;
        Random rnd = new Random();
        try {
            int sleepTime = rnd.nextInt(maxTimeout);
            System.out.println("[\u001B[34mDEBUG\u001B[0m] \u001B[31mS L E E P I N G\u001B[0m for " + sleepTime + "ms");
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server received PUT request in %s, %s", Thread.currentThread().getName(), request);
        }

        this.serverState.put(request.getNewTuple());                // add tuple to tuple space

        TupleSpacesOuterClass.PutResponse response = 
            TupleSpacesOuterClass.PutResponse.newBuilder().setOk("OK").build(); // construct a new Protobuffer object to send as response

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server sending PUT response in %s, OK\n\n", Thread.currentThread().getName());
        }
        responseObserver.onNext(response);                          // use the responseObserver to send the response
        responseObserver.onCompleted();                             // after sending the response, complete the call
    }

    @Override
    public void read(TupleSpacesOuterClass.ReadRequest request, StreamObserver<TupleSpacesOuterClass.ReadResponse> responseObserver) {
        final int maxTimeout = 5000;
        Random rnd = new Random();
        try {
            int sleepTime = rnd.nextInt(maxTimeout);
            System.out.println("[\u001B[34mDEBUG\u001B[0m] \u001B[31mS L E E P I N G\u001B[0m for " + sleepTime + "ms");
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server received READ request in %s, %s", Thread.currentThread().getName(), request);
        }

        String tuple = this.serverState
                            .read(request.getSearchPattern());      // read tuple from tuple space

        TupleSpacesOuterClass.ReadResponse response = 
            TupleSpacesOuterClass.ReadResponse
                                .newBuilder()
                                .setResult(tuple)
                                .build();                           // construct a new Protobuffer object to send as response

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server sending READ response in %s, %s\n\n", Thread.currentThread().getName(), tuple);
        }
        
        responseObserver.onNext(response);                          // use the responseObserver to send the response
        responseObserver.onCompleted();                             // after sending the response, complete the call
    }

    @Override
    public void take(TupleSpacesOuterClass.TakeRequest request, StreamObserver<TupleSpacesOuterClass.TakeResponse> responseObserver) {
        final int maxTimeout = 5000;
        Random rnd = new Random();
        try {
            int sleepTime = rnd.nextInt(maxTimeout);
            System.out.println("[\u001B[34mDEBUG\u001B[0m] \u001B[31mS L E E P I N G\u001B[0m for " + sleepTime + "ms");
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server received TAKE request in %s, %s", Thread.currentThread().getName(), request);
        }

        String tuple = this.serverState
                            .take(request.getSearchPattern());      // take tuple from tuple space

        TupleSpacesOuterClass.TakeResponse response = 
            TupleSpacesOuterClass.TakeResponse
                                .newBuilder()
                                .setResult(tuple)
                                .build();                           // construct a new Protobuffer object to send as response

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server sending TAKE response in %s, %s\n\n", Thread.currentThread().getName(), tuple);
        }

        responseObserver.onNext(response);                          // use the responseObserver to send the response
        responseObserver.onCompleted();                             // after sending the response, complete the call
    }

    @Override
    public synchronized void requestLock(TupleSpacesOuterClass.LockRequest request, StreamObserver<TupleSpacesOuterClass.LockResponse> responseObserver) {
        int clientId = request.getClientId();
        boolean granted = false;

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server received \u001B[34mLOCK\u001B[0m request in %s, %s", Thread.currentThread().getName(), request);
        }

        granted = serverState.requestLock(clientId);

        // Send response
        TupleSpacesOuterClass.LockResponse response =
            TupleSpacesOuterClass.LockResponse
                                .newBuilder()
                                .setGranted(granted)
                                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void releaseLock(TupleSpacesOuterClass.UnlockRequest request, StreamObserver<TupleSpacesOuterClass.UnlockResponse> responseObserver) {
        int clientId = request.getClientId();
        boolean released = false;

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server received \u001B[34mUNLOCK\u001B[0m request in %s, %s", Thread.currentThread().getName(), request);
        }

        this.serverState.releaseLock();

        // Send response
        TupleSpacesOuterClass.UnlockResponse response =
            TupleSpacesOuterClass.UnlockResponse
                                .newBuilder()
                                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTupleSpacesState(TupleSpacesOuterClass.getTupleSpacesStateRequest request, StreamObserver<TupleSpacesOuterClass.getTupleSpacesStateResponse> responseObserver) {
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server received GET-TUPLE-SPACES-STATE request in %s%n", Thread.currentThread().getName());
        }

        List<String> tupleSpacesState = this.serverState
                                            .getTupleSpacesState(); // get tuple space state

        TupleSpacesOuterClass.getTupleSpacesStateResponse.Builder responseBuilder = 
                TupleSpacesOuterClass.getTupleSpacesStateResponse.
                                    newBuilder();                   // create a response builder object to build the response

        for (String tuple : tupleSpacesState) {
            responseBuilder.addTuple(tuple);                        // keep adding tuples to response
        }

        TupleSpacesOuterClass.getTupleSpacesStateResponse response = responseBuilder.build();   // build the response

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server sending GET-TUPLE-SPACES-STATE response in %s, result %s\n\n", Thread.currentThread().getName(), tupleSpacesState.toString());
        }

        responseObserver.onNext(response);                          // use the responseObserver to send the response
        responseObserver.onCompleted();                             // after sending the response, complete the call
    }
}

