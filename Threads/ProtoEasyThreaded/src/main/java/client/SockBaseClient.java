package client;

import java.net.*;
import java.io.*;

import buffers.OperationProtos.*;

class SockBaseClient {
     

     public static void main (String args[]) throws Exception {
        Socket serverSock = null;
        OutputStream out = null;
        InputStream in = null;
        int port = 9099; // default port

        if (args.length != 2) {
            System.out.println("Expected arguments: <host(String)> <port(int)>");
            System.exit(1);
        }
        String host = args[0];
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }

        try {
            // connect to the server
            serverSock = new Socket(host, port);

            // output to the server
            out = serverSock.getOutputStream();

            // input from the server
            in = serverSock.getInputStream();

            // I am keeping this very simple and just calling one thing after another

            //ECHO
            Request.Builder echoRequestBuilder = Request.newBuilder();
            echoRequestBuilder.setType(Type.ECHO);
            echoRequestBuilder.setMessage("Hello there");
            echoRequestBuilder.build().writeDelimitedTo(out); // writing to server

            Response echoResp = Response.parseDelimitedFrom(in); // reading from server
            System.out.println(echoResp.toString());
            System.out.println("\n=========== \nPrinting response echo: " + echoResp.getResponse());

            // ADD request
            Request.Builder addRequest = Request.newBuilder();
            addRequest.setType(Type.ADD);
            addRequest.setNum1(40);
            addRequest.setNum2(2);

            addRequest.build().writeDelimitedTo(out); // writing to server

            Response addResp = Response.parseDelimitedFrom(in);
            System.out.println("\n\n=========== \nPrinting response add: " + addResp.getResult());

            // ADD MANY
            Request.Builder addManyRequest = Request.newBuilder();
            addManyRequest.setType(Type.ADDMANY);
            addManyRequest.addNums(1);
            addManyRequest.addNums(2);
            addManyRequest.addNums(3);


            Request req = addManyRequest.build(); // building (could also build later)
            req.writeDelimitedTo(out);

            Response res = Response.parseDelimitedFrom(in);
            System.out.println("\n\n =========== \nPrinting response ADDMANY");
            System.out.println(res.toString());
            System.out.println(res.getResult());


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)   in.close();
            if (out != null)  out.close();
            if (serverSock != null) serverSock.close();
        }
    }
}

