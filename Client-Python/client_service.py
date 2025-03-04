import sys
sys.path.insert(1, '../Contract/target/generated-sources/protobuf/python')

import grpc
import TupleSpaces_pb2 as pb2
import TupleSpaces_pb2_grpc as pb2_grpc


class ClientService:
    def __init__(self, host_port: str, client_id: int, debug: bool):
        self.DEBUG = debug
        self.host_port = host_port
        self.client_id = client_id
        self.channel = grpc.insecure_channel(self.host_port)
        self.stub = pb2_grpc.TupleSpacesStub(self.channel)
    
    def request_put(self, tuple_data: str):
        request = pb2.PutRequest(newTuple=tuple_data)   # create the request

        if self.DEBUG:
            print(f"[\u001B[34mDEBUG\u001B[0m] Client {self.client_id} sending PUT request... tuple: {tuple_data}", file=sys.stderr)

        try:
            response = self.stub.put(request)           # send the request
            print("OK\n")
        except grpc.RpcError as e:
            if self.DEBUG:
                print(f"[\u001B[31mERROR\u001B[0m] Client {self.client_id} PUT request u001B[31merror\u001B[0m: {e.details}", file=sys.stderr)

    def request_read(self, pattern: str) -> str:
        request = pb2.ReadRequest(searchPattern=pattern)    # create the request

        if self.DEBUG:
            print(f"[\u001B[34mDEBUG\u001B[0m] Client {self.client_id} sending READ request... pattern: {pattern}", file=sys.stderr)

        try:
            response = self.stub.read(request)                  # send the request
            print("OK")
            return response.result
        except grpc.RpcError as e:
            if self.DEBUG:
                print(f"[\u001B[31mERROR\u001B[0m] Client {self.client_id} READ request error: {e.details}", file=sys.stderr)
            return None

    def request_take(self, pattern: str) -> str:
        request = pb2.TakeRequest(searchPattern=pattern)        # create the request

        if self.DEBUG:
            print(f"[\u001B[34mDEBUG\u001B[0m] Client {self.client_id} sending TAKE request... pattern: {pattern}", file=sys.stderr)

        try:
            response = self.stub.take(request)                  # send the request
            print("OK")
            return response.result
        except grpc.RpcError as e:
            if self.DEBUG:
                print(f"[\u001B[31mERROR\u001B[0m] Client {self.client_id} TAKE request error: {e.details}", file=sys.stderr)
            return None    

    def request_get_tuple_spaces_state(self):
        request = pb2.getTupleSpacesStateRequest()              # create the request

        if self.DEBUG:
            print(f"[\u001B[34mDEBUG\u001B[0m] Client {self.client_id} sending GET_TUPLE_SPACES_STATE request...", file=sys.stderr)

        try:
            response = self.stub.getTupleSpacesState(request)   # send the request
            print("OK")
            return response.tuple
        except grpc.RpcError as e:
            if self.DEBUG:
                print(f"[\u001B[31mERROR\u001B[0m] Client {self.client_id} GET_TUPLE_SPACES_STATE request error: {e.details}", file=sys.stderr)
            return None

    def shutdown(self):
        self.channel.close()
        