## Purpose

This example demonstrates a very simple request/response protocol built with Protocol Buffers and Java sockets. This is the exact same as Sockets/ProtoEasy but this one is threaded in a simple and easy way just to give you a basic idea how you can very easily thread a server. 

The goal is not to show the "best" protobuf design. The goal is to show the basic workflow and keep it similar to JSON

which we covered in detail:

1. Define messages in a `.proto` file.
2. Generate Java classes from that file (gradle generateProto)
3. Build protobuf requests on the client.
4. Send them over a socket.
5. Parse them on the server and send protobuf responses back.

To keep the example small, the client sends three hardcoded requests one after another:

- `ECHO`
- `ADD`
- `ADDMANY`

This is intentionally closer to the style of a simple JSON-based protocol. 
When we get to GRPC at the end of the course we will talk about proto again and then have a bit different messages. 

Error handling is intentionally minimal so the focus stays on how protobuf requests and responses are built and exchanged.

## What To Look At

- [src/main/proto/operation.proto](src/main/proto/operation.proto): the protocol definition
- [src/main/java/client/SockBaseClient.java](src/main/java/client/SockBaseClient.java): how requests are built and sent
- [src/main/java/server/SockBaseServer.java](src/main/java/server/SockBaseServer.java): how requests are parsed and responses are created

One useful detail in this example is the use of `writeDelimitedTo(...)` and `parseDelimitedFrom(...)`. 
Those methods let multiple protobuf messages be sent across the same socket stream.

## Generating The Proto Code

Run:

```bash
gradle generateProto
```

The generated Java code will appear under `build/generated/source/proto/main/java/buffers`.

You usually do not need to read the generated files for this example. 
Focus on the `.proto` file and the client/server code first, look through the lecture slides to know which methods are available. 

## Running The Example

By default, the server uses port `9099`.

Start the server:

```bash
gradle runServer
```

Then run the client:

```bash
gradle runClient --console=plain -q
```

If you want to choose a different host or port, use:

```bash
gradle runClient -Pport=9099 -Phost='localhost'
```

```bash
gradle runServer -Pport=9099
```

## Notes

- This example uses `proto2` and optional fields because that keeps the "message with some fields present and some absent" idea easy to see.
- The protocol is intentionally simple and compact for teaching.
- A more realistic design would usually use more specific request and response message types.
