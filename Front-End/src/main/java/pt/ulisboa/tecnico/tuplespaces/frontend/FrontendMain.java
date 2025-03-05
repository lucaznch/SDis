package pt.ulisboa.tecnico.tuplespaces.frontend;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.lang.InterruptedException;

/*
The frontend is on the path between clients and the server.

Note:   The system must support having multiple front-ends running, 
        each serving a subset of clients.
        However, in the project we will only test the case of one front-end.

The front-end is simultaneously a server (as it receives and responds to client requests)
and a client (as it makes remote invocations to the TupleSpaces server(s).

When launched, it receives the port on which it should offer its remote service,
as well as the hostname and port pairs of the TupleSpaces servers it will interact with
(one server in variant A, three servers in the following variants).

variant A: $ mvn exec:java -Dexec.args="2001 localhost:3001"
- 2001 is the port on which the front-end will offer its remote service, 
- localhost:3001 is the hostname and port of the TupleSpaces server

variant B, C: $ mvn exec:java -Dexec.args="2001 localhost:3001 localhost:3002 localhost:3003" 
- 2001 is the port on which the front-end will offer its remote service,
- localhost:3001, localhost:3002, and localhost:3003 are the hostnames and ports of the TupleSpaces servers.
*/

public class FrontendMain {

    private static int port;       // port used for client communication
    private static String target;  // <host:port> used for server communication



    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println(FrontendMain.class.getSimpleName());

        // check arguments
        if (args.length != 2 && args.length != 3) {
            System.err.println("Invalid number of arguments");
            System.err.printf("Usage: java %s <port> <host:port> [-debug]%n", FrontendMain.class.getName());
            return;
        }

        boolean DEBUG = (args.length == 3 && args[2].equals("-debug"));

        if (DEBUG) {
            System.err.println("[\u001B[34mDEBUG\u001B[0m] Debug mode enabled");
        
            // receive and print arguments
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Received %d arguments%n", args.length);
            for (int i = 0; i < args.length; i++) {
                System.err.printf("[\u001B[34mDEBUG\u001B[0m] arg[%d] = %s%n", i, args[i]);
            }
        }

        port = Integer.parseInt(args[0]);
        target = args[1];

        // create the server for client communication
        Server server = ServerBuilder.forPort(port).addService(new FrontendImpl(DEBUG, target)).build();

        server.start();

        if (DEBUG) {
            System.err.printf("[\u001B[34mDEBUG\u001B[0m] Frontend started, listening on port: %d\n\n", port);
        }

        server.awaitTermination();
    }
}

