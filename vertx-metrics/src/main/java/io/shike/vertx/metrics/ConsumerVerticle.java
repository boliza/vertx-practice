package io.shike.vertx.metrics;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class ConsumerVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions(), cv -> {
      if (cv.succeeded()) {
        cv.result().deployVerticle(new ConsumerVerticle(), dv -> System.out.println("deploy: " + dv.result()));
      }
    });
  }

  @Override
  public void start() throws Exception {
    for (String address : Startup.addresses) {
      vertx.eventBus().consumer(address, event -> {
        System.out.println(event.address() + " consumer message: " + event.body());
        event.reply("Reply:" + event.body());
      });
    }
    super.start();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
