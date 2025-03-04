package pt.ulisboa.tecnico.tuplespaces.server;

// classes generated from the proto file
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.TupleSpacesGrpc;
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.TupleSpacesOuterClass;

import pt.ulisboa.tecnico.tuplespaces.server.domain.ServerState;

import java.util.List;

import io.grpc.stub.StreamObserver;


public class TupleSpacesServiceImpl extends TupleSpacesGrpc.TupleSpacesImplBase {

    private ServerState serverState;

    public TupleSpacesServiceImpl() {
        this.serverState = new ServerState();
    }


    @Override
    public void put(TupleSpacesOuterClass.PutRequest request, StreamObserver<TupleSpacesOuterClass.PutResponse> responseObserver) {
        System.out.println(request);

        this.serverState.put(request.getNewTuple());
    
        TupleSpacesOuterClass.PutResponse response = TupleSpacesOuterClass.PutResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void read(TupleSpacesOuterClass.ReadRequest request, StreamObserver<TupleSpacesOuterClass.ReadResponse> responseObserver) {
        System.out.println(request);

        String tuple = this.serverState.read(request.getSearchPattern());

        TupleSpacesOuterClass.ReadResponse response = TupleSpacesOuterClass.ReadResponse.newBuilder().setResult(tuple).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void take(TupleSpacesOuterClass.TakeRequest request, StreamObserver<TupleSpacesOuterClass.TakeResponse> responseObserver) {
        System.out.println(request);

        String tuple = this.serverState.take(request.getSearchPattern());

        TupleSpacesOuterClass.TakeResponse response = TupleSpacesOuterClass.TakeResponse.newBuilder().setResult(tuple).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTupleSpacesState(TupleSpacesOuterClass.getTupleSpacesStateRequest request, StreamObserver<TupleSpacesOuterClass.getTupleSpacesStateResponse> responseObserver) {
        System.out.println(request);

        List<String> tupleSpacesState = this.serverState.getTupleSpacesState();

        TupleSpacesOuterClass.getTupleSpacesStateResponse.Builder responseBuilder = TupleSpacesOuterClass.getTupleSpacesStateResponse.newBuilder();
        for (String tuple : tupleSpacesState) {
            responseBuilder.addTuple(tuple);
        }
        TupleSpacesOuterClass.getTupleSpacesStateResponse response = responseBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

