package pt.ulisboa.tecnico.tuplespaces.frontend;

// classes generated from the proto file
import pt.ulisboa.tecnico.tuplespaces.replicated.contract.TupleSpacesGrpc;
import pt.ulisboa.tecnico.tuplespaces.replicated.contract.TupleSpacesOuterClass;

import io.grpc.stub.StreamObserver;






public class FrontendUnlockObserver implements StreamObserver<TupleSpacesOuterClass.UnlockResponse> {
    private final int serverId;
    private final int requestId;
    private final String request;
    private final int retryId;
    private ResponseCollector collector;

    public FrontendUnlockObserver(int serverId, int requestId, String request, int retryId, ResponseCollector c) {
        this.serverId = serverId;
        this.requestId = requestId;
        this.request = request;
        this.retryId = retryId;
        this.collector = c;
    }

    @Override
    public void onNext(TupleSpacesOuterClass.UnlockResponse response) {
        collector.addLockResponse("UNLOCK", this.requestId, this.request, null, this.serverId, this.retryId);
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("UNLOCK error: " + t.getMessage());
    }

    @Override
    public void onCompleted() {
        // System.out.printf("[\u001B[34mDEBUG\u001B[0m] FrontendUnlockObserver: UNLOCK completed (#%d)", this.requestId);
    }
}






