package io.shike.vertx.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class ProducerVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions(), cv -> {
      if (cv.succeeded()) {
        cv.result().deployVerticle(new ProducerVerticle(), dv -> System.out.println("deploy: " + dv.result()));
      }
    });
  }

  @Override
  public void start() throws Exception {
    for (int i = 0; i < 100; i++) {
      vertx.eventBus().send(Startup.addresses.get(i % 2), "message-" + i, event -> {
        if (event.failed()) {
          System.out.println(event.cause());
        }
      });
    }
    super.start();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
