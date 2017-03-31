package io.shike.vertx.proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author Ranger Tsao(http://www.mowenxi.com)
 */
public class Proxy extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(Proxy.class);

    private HttpServer server;
    private HttpClient client;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        client = vertx.createHttpClient(new HttpClientOptions());
        server = vertx.createHttpServer()
                      .websocketHandler(this::proxyWebSocket)
                      .requestHandler(this::proxyHttp)
                      .listen(8181);
        logger.info("start proxy server");
        startFuture.complete();
    }

    private void proxyWebSocket(ServerWebSocket ws) {
        ws.handler(data -> {
            logger.info("proxy message:" + data.toString("utf-8") + " , from path:" + ws.path());
            client.websocket(8080, "localhost", "/some-uri", websocket -> {
                websocket.handler(proxy -> {
                    logger.info("server message:" + proxy.toString("utf-8"));
                    ws.writeBinaryMessage(proxy);
                });
                websocket.writeBinaryMessage(data);
            });
        });
    }

    private void proxyHttp(HttpServerRequest request) {
        logger.info("proxying request:" + request.uri());
        HttpClientRequest proxyRequest = client.request(request.method(),
                                                        8080,
                                                        "localhost",
                                                        request.uri(),
                                                        response -> {
                                                            logger.info("proxying response: " + response.statusCode());
                                                            request.response().setChunked(true);
                                                            request.response().setStatusCode(response.statusCode());
                                                            request.response().headers().setAll(response.headers());
                                                            response.handler(data -> request.response().write(data));
                                                            response.endHandler((v) -> request.response().end());
                                                        });
        proxyRequest.setChunked(true);
        proxyRequest.headers().setAll(request.headers());
        request.handler(data -> proxyRequest.write(data));
        request.endHandler((v) -> proxyRequest.end());
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        client.close();
        server.close(stopFuture.completer());
    }
}
