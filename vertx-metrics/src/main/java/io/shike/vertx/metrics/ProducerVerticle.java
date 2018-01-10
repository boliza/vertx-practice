package io.shike.vertx.metrics;

import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class ProducerVerticle extends AbstractVerticle {

  private AtomicInteger i = new AtomicInteger(0);

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions(), cv -> {
      if (cv.succeeded()) {
        cv.result().deployVerticle(new ProducerVerticle(), dv -> System.out.println("deploy: " + dv.result()));
      }
    });
  }

  @Override
  public void start() throws Exception {
    vertx.setPeriodic(100, ph -> {
      vertx.eventBus().send(Startup.addresses.get(i.getAndIncrement() % 2), "message-" + i.get(),
                            event -> {
                              if (event.failed()) {
                                System.out.println(event.cause().getMessage());
                              }
                            });
    });
    super.start();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
