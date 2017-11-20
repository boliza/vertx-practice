package io.shike.vertx.eventbus;

import io.vertx.core.AbstractVerticle;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class RestVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    vertx.createHttpServer()
         .requestHandler(request -> {
           vertx.eventBus().send("eb1", request.remoteAddress().host(), ebr -> {
             if (ebr.succeeded()) {
               request.response().write(ebr.result().body().toString());
             } else {
               request.response().setStatusCode(500).write("not ok");
             }
           });
         })
         .listen(8080, lr -> {

         });
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
