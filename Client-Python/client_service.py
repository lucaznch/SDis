import sys
sys.path.insert(1, '../Contract/target/generated-sources/protobuf/python')

import grpc
import TupleSpaces_pb2 as pb2
import TupleSpaces_pb2_grpc as pb2_grpc


class ClientService:
    def __init__(self, host_port: str, client_id: int):
        self.host_port = host_port
        self.client_id = client_id
        self.channel = grpc.insecure_channel(self.host_port)
        self.stub = pb2_grpc.TupleSpacesStub(self.channel)
    
    def request_put(self, tuple_data: str):
        request = pb2.PutRequest(newTuple=tuple_data)
        response = self.stub.put(request)
    
    def request_read(self, pattern: str) -> str:
        request = pb2.ReadRequest(searchPattern=pattern)
        response = self.stub.read(request)
        return response.result

    def request_take(self, pattern: str) -> str:
        request = pb2.TakeRequest(searchPattern=pattern)
        response = self.stub.take(request)
        return response.result
    
    def request_get_tuple_spaces_state(self):
        request = pb2.getTupleSpacesStateRequest()
        response = self.stub.getTupleSpacesState(request)
        return response.tuple
    
    def shutdown(self):
        self.channel.close()
        