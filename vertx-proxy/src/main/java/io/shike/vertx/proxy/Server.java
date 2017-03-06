package io.shike.vertx.proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author Ranger Tsao(http://www.mowenxi.com)
 */
public class Server extends AbstractVerticle {

    HttpServer httpServer;
    private Logger logger = LoggerFactory.getLogger(Server.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        httpServer = vertx.createHttpServer()
                          .websocketHandler(ws -> ws.handler(ws::writeBinaryMessage))
                          .requestHandler(req -> {
                              if (req.uri().equals("/")) {
                                  req.response().sendFile("ws.html");
                              }
                          })
                          .listen(8080);
        logger.info("start server");
        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        httpServer.close(stopFuture.completer());
    }
}
