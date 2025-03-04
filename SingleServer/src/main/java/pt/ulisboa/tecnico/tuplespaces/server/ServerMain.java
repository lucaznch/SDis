package pt.ulisboa.tecnico.tuplespaces.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.lang.InterruptedException;


public class ServerMain {

    private static int port;

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println(ServerMain.class.getSimpleName());

        // receive and print arguments
        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }

        // check arguments
        if (args.length != 1) {
            System.err.println("Invalid number of arguments");
            System.err.printf("Usage: java %s <port>%n", ServerMain.class.getName());
            return;
        }

        port = Integer.parseInt(args[0]);

        Server server = ServerBuilder.forPort(port).addService(new TupleSpacesServiceImpl()).build();

        server.start();

        System.out.printf("Server started, listening on %d%n", port);

        server.awaitTermination();
    }
}

