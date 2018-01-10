vertx.createHttpServer().requestHandler({ req ->
  req.response()
    .putHeader("content-type", "text/plain")
    .end("Hello from Vert.x!")
}).listen(8080,"0.0.0.0")
