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

  private Logger logger = LoggerFactory.getLogger(Server.class);

  private HttpServer httpServer1;
  private HttpServer httpServer2;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    httpServer1 = vertx.createHttpServer()
                       .websocketHandler(ws -> ws.handler(data -> {
                         data.appendString(", from server1");
                         ws.writeBinaryMessage(data);
                       }))
                       .requestHandler(req -> {
                         if (req.uri().equals("/ws1")) {
                           req.response().sendFile("ws1.html");
                         }
                       })
                       .listen(8080);
    logger.info("start server1");

    httpServer2 = vertx.createHttpServer()
                       .websocketHandler(ws -> ws.handler(data -> {
                         data.appendString(", from server2");
                         ws.writeBinaryMessage(data);
                       }))
                       .requestHandler(req -> {
                         if (req.uri().equals("/ws2")) {
                           req.response().sendFile("ws2.html");
                         }
                       })
                       .listen(8081);
    logger.info("start server2");

    startFuture.complete();
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    httpServer1.close(stopFuture.completer());
    httpServer2.close(stopFuture.completer());
  }
}
