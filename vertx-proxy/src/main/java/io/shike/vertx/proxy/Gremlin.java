package io.shike.vertx.proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class Gremlin extends AbstractVerticle {

  private Logger logger = LoggerFactory.getLogger(Proxy.class);
  private HttpServer server;
  private HttpClient client;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(Gremlin.class.getName());
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    client = vertx.createHttpClient(new HttpClientOptions());
    server = vertx.createHttpServer()
                  .websocketHandler(this::proxyWebSocket)
                  .listen(8181);
    logger.info("start gremlin proxy server");
    startFuture.complete();
  }

  private void proxyWebSocket(ServerWebSocket ws) {
    //TODO decode gremlin frame
    ws.handler(data -> {
      logger.info("proxy message:" + data.toString("utf-8") + " , from path:" + ws.path());
      client.websocket(8182, "localhost", "/gremlin", websocket -> {
        websocket.handler(proxy -> {
          logger.info("server message:" + proxy.toString("utf-8"));
          ws.writeBinaryMessage(proxy);
        });
        websocket.writeBinaryMessage(data);
      });
    });
  }


  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    client.close();
    server.close(stopFuture.completer());
  }
}
