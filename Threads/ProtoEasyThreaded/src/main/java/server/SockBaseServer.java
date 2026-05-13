package server;
import java.net.*;
import java.io.*;

import buffers.OperationProtos.*;

class SockBaseServer implements Runnable  { // setup the threading for the class
    // Moved in and out stream here so that every thread will have their own
    // socket and in and out stream
    InputStream in = null;
    OutputStream out = null;
    Socket clientSocket = null;
    // Simple constructor so that the thread class actually has the clientSocket
    // could also move in and out stream setting in here, I do it in run
    // either would be ok
    public SockBaseServer(Socket s) {
        clientSocket = s;
    }
    public static void main (String args[]) throws Exception {

        ServerSocket    serv = null;

        int port = 9099; // default port
        if (args.length != 1) {
          System.out.println("Expected arguments: <port(int)>");
          System.exit(1);
		}
        
        try {
          port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
          System.exit(2);
        }
        try {
            serv = new ServerSocket(port);
        } catch(Exception e) {
          e.printStackTrace();
          System.exit(2);
        }
        // this should always be in main thread, that one always waits for
        // new connections creates the thread.
        // thread then handles the client server communication
        while (serv.isBound() && !serv.isClosed()) {
            System.out.println("Ready...");
            Socket clientSocket = serv.accept();
            Thread.sleep(6000); // just so we actually see that there is more than on client
            SockBaseServer newServer = new SockBaseServer(clientSocket);
            Thread newThread = new Thread(newServer);
            newThread.start();
        }
    }

    public void run() {
        try {
            // creating the streams to communicate
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
            while(true) { // the loop to figure out what the client wants
                Request req = Request.parseDelimitedFrom(in);
                if (req == null) {
                    System.out.println("Client disconnected");
                    break;
                }
                System.out.println("Received request: \n" + req);
                int sum = 0;
                String returnString = "";

                Response.Builder b = Response.newBuilder();

                Thread.sleep(6000);
                if (req.getType() == Type.ECHO) {
                    System.out.println("Echo");
                    b.setType(Type.ECHO);
                    if (req.hasMessage()) {
                        System.out.println(req.getMessage());
                        returnString = req.getMessage();
                        b.setResponse(returnString);
                    } else {
                        //error handle this
                    }
                } else if (req.getType() == Type.ADD) {
                    b.setType(Type.ADD);
                    System.out.println("ADD");
                    sum = req.getNum1() + req.getNum2();
                    b.setResult(sum);
                } else if (req.getType() == Type.ADDMANY) {
                    b.setType(Type.ADDMANY);
                    System.out.println("ADDMANY");
                    System.out.println("How many numbers in list?: "+ req.getNumsCount());
                    for (int i : req.getNumsList()) {
                        sum += i;
                    }
                    b.setResult(sum);
                }

                Response res = b.build();

                res.writeDelimitedTo(out);
            }

        } catch (Exception ex) {
            ex.printStackTrace(); // not handled pretty
            System.out.println("Client disconnected");
        }
    }

}
