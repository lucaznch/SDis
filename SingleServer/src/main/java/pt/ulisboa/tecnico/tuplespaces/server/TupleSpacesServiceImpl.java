package pt.ulisboa.tecnico.tuplespaces.server;

// classes generated from the proto file
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.TupleSpacesGrpc;
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.TupleSpacesOuterClass;

import pt.ulisboa.tecnico.tuplespaces.server.domain.ServerState;

import java.util.List;

import io.grpc.stub.StreamObserver;


public class TupleSpacesServiceImpl extends TupleSpacesGrpc.TupleSpacesImplBase {

    private boolean DEBUG;
    private int numberOfRequests = 1;
    private int numberPutRequests = 1;
    private int numberReadRequests = 1;
    private int numberTakeRequests = 1;
    private int numberGetTupleSpacesStateRequests = 1;
    private ServerState serverState;

    public TupleSpacesServiceImpl(boolean debug) {
        this.DEBUG = debug;
        this.serverState = new ServerState(debug);
    }


    @Override
    public void put(TupleSpacesOuterClass.PutRequest request, StreamObserver<TupleSpacesOuterClass.PutResponse> responseObserver) {
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server received PUT request (#%d) in %s, %s", this.numberPutRequests, Thread.currentThread().getName(), request);
        }

        this.serverState.put(request.getNewTuple());
    
        TupleSpacesOuterClass.PutResponse response = TupleSpacesOuterClass.PutResponse.newBuilder().build();

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server sending PUT response (#%d) in %s\n\n", this.numberPutRequests++, Thread.currentThread().getName());
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void read(TupleSpacesOuterClass.ReadRequest request, StreamObserver<TupleSpacesOuterClass.ReadResponse> responseObserver) {
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server received READ request (#%d) in %s, %s", this.numberReadRequests, Thread.currentThread().getName(), request);
        }

        String tuple = this.serverState.read(request.getSearchPattern());

        TupleSpacesOuterClass.ReadResponse response = TupleSpacesOuterClass.ReadResponse.newBuilder().setResult(tuple).build();

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server sending READ response (#%d) in %s, result: %s\n\n", this.numberReadRequests++, Thread.currentThread().getName(), tuple);
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void take(TupleSpacesOuterClass.TakeRequest request, StreamObserver<TupleSpacesOuterClass.TakeResponse> responseObserver) {
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server received TAKE request (#%d) in %s, %s", this.numberTakeRequests, Thread.currentThread().getName(), request);
        }

        String tuple = this.serverState.take(request.getSearchPattern());

        TupleSpacesOuterClass.TakeResponse response = TupleSpacesOuterClass.TakeResponse.newBuilder().setResult(tuple).build();

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server sending TAKE response (#%d) in %s, result: %s\n\n", this.numberTakeRequests++, Thread.currentThread().getName(), tuple);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTupleSpacesState(TupleSpacesOuterClass.getTupleSpacesStateRequest request, StreamObserver<TupleSpacesOuterClass.getTupleSpacesStateResponse> responseObserver) {
        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server received GET-TUPLE-SPACES-STATE request (#%d) in %s%n", this.numberGetTupleSpacesStateRequests, Thread.currentThread().getName());
        }

        List<String> tupleSpacesState = this.serverState.getTupleSpacesState();

        TupleSpacesOuterClass.getTupleSpacesStateResponse.Builder responseBuilder = TupleSpacesOuterClass.getTupleSpacesStateResponse.newBuilder();
        for (String tuple : tupleSpacesState) {
            responseBuilder.addTuple(tuple);
        }
        TupleSpacesOuterClass.getTupleSpacesStateResponse response = responseBuilder.build();

        if (this.DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Server sending GET-TUPLE-SPACES-STATE response (#%d) in %s, result %s\n\n", this.numberGetTupleSpacesStateRequests++, Thread.currentThread().getName(), tupleSpacesState.toString());
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

