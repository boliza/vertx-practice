package io.shike.vertx.async;

import java.util.concurrent.CountDownLatch;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class Startup extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Startup());
  }

  @Override
  public void start() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    vertx.executeBlocking(h -> {
                            h.complete("done");
                            latch.countDown();
                          },
                          r -> System.out.println(r.result()));
    latch.await();
    System.out.println("start finished");
  }

  @Override
  public void stop() throws Exception {
  }
}
