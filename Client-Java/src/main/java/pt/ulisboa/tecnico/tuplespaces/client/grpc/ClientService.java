package pt.ulisboa.tecnico.tuplespaces.client.grpc;

// classes generated from the proto file
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.TupleSpacesGrpc;
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.TupleSpacesOuterClass;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;


public class ClientService {

    private final int client_id;
    private final String host_port;
    private final ManagedChannel channel;
    private final TupleSpacesGrpc.TupleSpacesBlockingStub stub;

    public ClientService(String host_port, int client_id) {
        this.client_id = client_id;
        this.host_port = host_port;
        this.channel = ManagedChannelBuilder.forTarget(host_port).usePlaintext().build();
        this.stub = TupleSpacesGrpc.newBlockingStub(channel);
    }


    public void requestPut(String tuple) {
        TupleSpacesOuterClass.PutRequest request = TupleSpacesOuterClass.PutRequest.newBuilder().setNewTuple(tuple).build();
        TupleSpacesOuterClass.PutResponse response = stub.put(request);
    }

    public String requestRead(String pattern) {
        TupleSpacesOuterClass.ReadRequest request = TupleSpacesOuterClass.ReadRequest.newBuilder().setSearchPattern(pattern).build();
        TupleSpacesOuterClass.ReadResponse response = stub.read(request);
    
        return response.getResult();
    }

    public String requestTake(String pattern) {
        TupleSpacesOuterClass.TakeRequest request = TupleSpacesOuterClass.TakeRequest.newBuilder().setSearchPattern(pattern).build();
        TupleSpacesOuterClass.TakeResponse response = stub.take(request);
    
        return response.getResult();
    }

    public List<String> requestGetTupleSpacesState() {
        TupleSpacesOuterClass.getTupleSpacesStateRequest request = TupleSpacesOuterClass.getTupleSpacesStateRequest.newBuilder().build();
        TupleSpacesOuterClass.getTupleSpacesStateResponse response = stub.getTupleSpacesState(request);
    
        int i = 0;
        int tupleSpacesStateCount = response.getTupleCount();
        List<String> tupleSpacesState = new ArrayList<>();

        while (i < tupleSpacesStateCount) {
            tupleSpacesState.add(response.getTuple(i));
            i++;
        }

        return tupleSpacesState;
    }

    public void shutdown() {
        channel.shutdown();
    }
}
