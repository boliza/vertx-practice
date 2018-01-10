package io.shike.vertx.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class Consumer2Verticle extends AbstractVerticle {

  private final static String ID = "consumer2";

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions(), cv -> {
      if (cv.succeeded()) {
        cv.result().deployVerticle(Consumer2Verticle.class, new DeploymentOptions().setHa(true), dv -> System.out.println("deploy: " + dv.result()));
      }
    });
  }

  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer("eb2", event -> {
      System.out.println("ID:+" + ID + " ," + event.address() + " consumer message: " + event.body());
      event.reply("ok");
    });
    super.start();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
