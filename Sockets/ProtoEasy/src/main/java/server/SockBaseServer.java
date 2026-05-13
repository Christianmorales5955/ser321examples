package server;
import java.net.*;
import java.io.*;

import buffers.OperationProtos.*;

class SockBaseServer {
    public static void main (String args[]) throws Exception {

        ServerSocket    serv = null;
        InputStream in = null;
        OutputStream out = null;
        Socket clientSocket = null;
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
        while (serv.isBound() && !serv.isClosed()) {
            System.out.println("Ready...");
            try {
                clientSocket = serv.accept();
                in = clientSocket.getInputStream();
                out = clientSocket.getOutputStream();

                while(true) {
                    Request req = Request.parseDelimitedFrom(in);
                    if (req == null) {
                        System.out.println("Client disconnected");
                        break;
                    }
                    System.out.println("Received request: \n" + req);
                    int sum = 0;
                    String returnString = "";

                    Response.Builder b = Response.newBuilder();

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
            } finally {
                if (out != null)  out.close();
                if (in != null)   in.close();
                if (clientSocket != null) clientSocket.close();
            }
        }
    }

}
